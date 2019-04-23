package nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import requests.AcceptRequest;
import requests.JoinNetworkRequest;
import requests.NodeIdRequest;
import requests.PrepareRequest;
import requests.ProposalRequest;
import responses.AcceptedResponse;
import responses.JoinNetworkResponse;
import responses.NodeIdResponse;
import responses.PromiseResponse;
import roles.AcceptorsInterface;
import roles.DistributedNodeInterface;
import roles.LearnerInterface;
import roles.ProposerInterface;
import structs.ConnectionDetails;

/**
 * @author Victor
 *
 */
public class DistributedNode implements DistributedNodeInterface, AcceptorsInterface, LearnerInterface, ProposerInterface {
	
	/**
	 * Represents the node's id to be used in determining the leader.
	 * The leader node has an id of 0, other nodes have a positive id > 0.
	 * Any node with a negative id is invalid and will be terminated eventually.
	 */
	public Integer nodeId;
	/**
	 * Represents the network leader's server IP without port
	 */
	public String paxosLeaderIp;
	/**
	 * Represents the network leader's server port
	 */
	public int paxosLeaderPort;
	/**
	 * Represents this machine's server IP without port
	 */
	public String serverIp;
	/**
	 * # of nodes needed to reach consensus
	 */
	public int majority;
	
	private boolean isPaxosLeader;
	private boolean paxosNetworkIsPaused; // used to stop the paxos network when a leader has died
	private DistributedNode distributedNode;
	private ServerSocket serverSocket; // socket is used for downloading logs and connecting to the server
	private ServerSocket paxosSocket; // socket is used for communicating on the paxos network
	private ServerSocket heartbeatSocket; // socket used to respond to heartbeat reqs
	
	private PriorityQueue<ProposalRequest> proposalsToServe;
	
	private TableUpdateLogger updateLog;
	private ConcurrentHashMap<Integer, ConnectionDetails> nodeConnections; // key: remote ip address, value: details of connection... is managed by paxos leader
	private Queue<Integer> reusableNodeIds;
	
	/**
	 * Initializes a distributed node on this machine
	 * @throws IOException
	 */
	public DistributedNode() throws IOException {
		this.nodeId = 0;
		this.isPaxosLeader = true;
		this.serverSocket = new ServerSocket(0);
		this.paxosSocket = new ServerSocket(0);
		this.heartbeatSocket = new ServerSocket(0);
		serverSocket.setReuseAddress(true);
		paxosSocket.setReuseAddress(true);
		heartbeatSocket.setReuseAddress(true);
		this.serverIp = getOutboundServerAddress();
		this.updateLog = new TableUpdateLogger();
		this.nodeConnections = new ConcurrentHashMap<Integer, ConnectionDetails>();
		this.reusableNodeIds = new LinkedList<Integer>();
		
		startServer();
		startPaxos();
		establishHeartbeat();
	}


	@Override
	public boolean joinNetwork(String remoteSocketAddress) {
		String[] vals = remoteSocketAddress.split(":");
		if (vals.length != 2) return false;
		Socket connection = null;
		try {
			connection = new Socket(vals[0].trim(),Integer.parseInt(vals[1].trim()));
			ObjectInputStream inStream = new ObjectInputStream(connection.getInputStream());
			ObjectOutputStream outStream = new ObjectOutputStream(connection.getOutputStream());
			
			JoinNetworkRequest connectReq = new JoinNetworkRequest(this.serverIp, this.paxosSocket.getLocalPort());
			outStream.writeObject(connectReq);
			outStream.flush();
			
			while (inStream.available() == 0) {
				// Wait for response from connection
				// TODO: Set a timeout
			}
			
			// Read packet and double check object class
			Object packet = inStream.readObject();
			JoinNetworkResponse connectRes = null;
			
			if (packet instanceof JoinNetworkResponse) {
				connectRes = (JoinNetworkResponse) packet;
			} else {
				return false;
			}
			
			// Save leader's information
			this.paxosLeaderIp = connectRes.paxosLeaderIp;
			this.paxosLeaderPort = connectRes.paxosLeaderPort;
			
			this.isPaxosLeader = false;
			
			connection.close();
			
			// Try connecting to leader and get full contact list of remote nodes
			connection = new Socket(paxosLeaderIp, paxosLeaderPort);
			inStream = new ObjectInputStream(connection.getInputStream());
			outStream = new ObjectOutputStream(connection.getOutputStream());
			
			outStream.writeObject(new NodeIdRequest());
			outStream.flush();
			// Read packet and double check object class
			while (inStream.available() == 0) {
				// Wait for response from connection
				// TODO: Set a timeout
			}
			packet = inStream.readObject();
			NodeIdResponse nodeIdRes = null;
			
			if (packet instanceof NodeIdResponse) {
				nodeIdRes = (NodeIdResponse) packet;
			} else {
				return false;
			}
			
			if (nodeIdRes.nodeId < 0) return false;
			this.nodeId = nodeIdRes.nodeId;
			this.nodeConnections.putAll(nodeIdRes.remoteConnections);
			
			connection.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}


	@Override
	public int electLeader() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean establishHeartbeat() {
		Thread heartbeat = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Socket clientSocket = heartbeatSocket.accept();
						while (!clientSocket.isInputShutdown()) { 
							// wait until disconnection
						}
					}
				} catch (IOException e) {
					terminate();
				}
			}
		});
		
		heartbeat.start();
		return true;
	}
	
	public void checkHeartbeats() {
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						// Run heartbeat checks every 10 secs and remove any that fail to respond
						Thread.sleep(10000);
						for (Integer key: nodeConnections.keySet()) {
							ConnectionDetails connDetails = nodeConnections.get(key);
							Socket heartbeatSocket = new Socket(connDetails.serverIp, connDetails.heartbeatPort);
							if (heartbeatSocket.isConnected()) {
								heartbeatSocket.close();
								continue;
							} else {
								nodeConnections.remove(key);
								recalculateMajority();
								reusableNodeIds.offer(key);
							}
						}
						Socket clientSocket = heartbeatSocket.accept();
						while (!clientSocket.isInputShutdown()) { 
							// wait until disconnection
						}
					}
				} catch (Exception e) {
					terminate();
				}
			}
		});
	}
	
	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		System.exit(0);
	}
	


	@Override
	public void proposeTransaction(ProposalRequest proposal) {
		if (isPaxosLeader) {
			prepareTransaction(proposal);
		} else {
			try {
				Socket leaderSocket = new Socket(paxosLeaderIp, paxosLeaderPort);
				ObjectOutputStream out = new ObjectOutputStream(leaderSocket.getOutputStream());
				out.writeObject(proposal);
				out.flush();
				leaderSocket.close();
			} catch (IOException e) {
				System.out.println("Proposal was not uploaded to leader");
			}			
		}
	}

	
	@Override
	public void prepareTransaction(ProposalRequest proposal) {
		AtomicInteger numAcks = new AtomicInteger(0);
		AtomicInteger numNacks = new AtomicInteger(0);
		PrepareRequest prepareReq = new PrepareRequest(proposal.proposalId);
		
		// Propose value for all acceptors and wait for ack/nack
		for (ConnectionDetails connDetails: this.nodeConnections.values()) {
			Thread prepareThread = new Thread(new Runnable() {
				public void run() {
					try {
						Socket remoteSocket = new Socket(connDetails.serverIp, connDetails.serverPort);
						ObjectOutputStream out = null;
						ObjectInputStream in = null;
						
						if (remoteSocket.isConnected()) {
							out = new ObjectOutputStream(remoteSocket.getOutputStream());
							in = new ObjectInputStream(remoteSocket.getInputStream());
						}
						out.writeObject(proposal);
						out.flush();
						while (in.available() == 0) {
							// Wait for reply or for connection to shut down
							if (remoteSocket.isInputShutdown()) {
								numNacks.getAndIncrement();
								return;
							}
						}
						
						Object res = in.readObject();
						if (res instanceof PromiseResponse) {
							if (((PromiseResponse) res).ack) {
								numAcks.getAndIncrement();
							}
						} else {
							numNacks.getAndIncrement();
						}
						remoteSocket.close();
					} catch (Exception e) {
						numNacks.getAndIncrement();
						// Stop operation because the heartbeat should take care of socket severance							
					}
				} 
			});
			
			prepareThread.start();
		}
		
		while (numAcks.get() < this.majority) {
			// Wait for responses
			if (numNacks.get() < this.majority) {
				return;
			}
		}
		
		// Sent out request to accept the change
		AcceptRequest acceptReq = new AcceptRequest(proposal.proposalId, cmds);
		requestAccept(acceptReq);
	}

	@Override
	public void requestAccept(AcceptRequest acceptReq) {
		AtomicInteger numAcks = new AtomicInteger(0);
		AtomicInteger numNacks = new AtomicInteger(0);
		
		for (ConnectionDetails connDetails: this.nodeConnections.values()) {
			new Thread(new Runnable() {
				public void run() {
					try {
						Socket remoteSocket = new Socket(connDetails.serverIp, connDetails.serverPort);
						ObjectOutputStream out = null;
						ObjectInputStream in = null;
						
						if (remoteSocket.isConnected()) {
							out = new ObjectOutputStream(remoteSocket.getOutputStream());
							in = new ObjectInputStream(remoteSocket.getInputStream());
						}
						out.writeObject(acceptReq);
						out.flush();
						while (in.available() == 0) {
							// Wait for reply or for connection to shut down
							if (remoteSocket.isInputShutdown()) {
								numNacks.getAndIncrement();
								return;
							}
						}
						
						Object res = in.readObject();
						if (res instanceof PromiseResponse) {
							if (((PromiseResponse) res).ack) {
								numAcks.getAndIncrement();
							}
						} else {
							numNacks.getAndIncrement();
						}
						remoteSocket.close();
					} catch (Exception e) {
						numNacks.getAndIncrement();
						// Stop operation because the heartbeat should take care of socket severance							
					}
				} 
			});
		}
		
		while (numAcks.get() < this.majority) {
			// Wait for responses
			if (numNacks.get() < this.majority) {
				return;
			}
		}
		
	}

	@Override
	public void promiseTransaction(PromiseResponse promise) {
		
	}


	@Override
	public void acceptProposal(AcceptedResponse acceptedRes) {
		
		
	}

	
	public DistributedNode getInstance() throws IOException {
		if (this.distributedNode == null) {
			this.distributedNode = new DistributedNode();
		}
		
		return this.distributedNode;
	}
	
	private boolean startServer() {
		// TODO Auto-generated method stub
		Thread server = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Socket clientSocket = serverSocket.accept();
						ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
						ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
						while (in.available() != 0 && !clientSocket.isInputShutdown()) { 
							// wait until package comes in
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		server.start();
		return true;
	}
	
	private boolean startPaxos() {
		Thread paxos = new Thread(new Runnable() {
			public void run() {
				try {
					if (isPaxosLeader) {
						checkHeartbeats();
					}
					while (!paxosNetworkIsPaused) {
						Socket clientSocket = paxosSocket.accept();
						ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
						ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
						while (in.available() != 0) { 
							// wait until package comes in
							if (clientSocket.isInputShutdown()) { // Assuming client never hangs up for no reason
								electLeader();
							}
						}
						
						Object packet = in.readObject();
						
						if (packet instanceof AcceptedResponse) {
							
						} else if (isPaxosLeader) {
							if (packet instanceof ProposalRequest) {
								
							} else if (packet instanceof PromiseResponse) {
								
							}
						} else { //A paxos follower
							if (packet instanceof PrepareRequest) {
								
							} else if (packet instanceof AcceptRequest) {
								
							}
						}
						
						clientSocket.close();
					}
				} catch (Exception e) {
					terminate();
				}
			}
		});
		
		paxos.start();
		return true;
	}
	
	public ServerSocket getServerSocket() {
		return this.serverSocket;
	}
	
	public ServerSocket getPaxosSocket() {
		return this.paxosSocket;
	}
	
	private String concatPortToIp(String ip, int port) {
		return String.format("%s:%d", ip, port);
	}
	
	private void recalculateMajority() {
		this.majority = (this.nodeConnections.size()/2)+1;
	}
	
	private String getOutboundServerAddress() throws IOException {
		URL ipCheck = new URL("http://checkip.amazonaws.com");
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(ipCheck.openStream()));
			String ip = in.readLine();
			return ip;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Do nothing
				}
			}
		}
	}
}
