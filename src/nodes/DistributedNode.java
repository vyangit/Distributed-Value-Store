package nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import commands.AbstractDistributedCommand;
import requests.AcceptRequest;
import requests.ElectionRequest;
import requests.JoinNetworkRequest;
import requests.NodeIdRequest;
import requests.PrepareRequest;
import requests.ProposalRequest;
import responses.AcceptedResponse;
import responses.ElectionResponse;
import responses.JoinNetworkResponse;
import responses.NodeIdResponse;
import responses.PromiseResponse;
import roles.AcceptorsInterface;
import roles.DistributedNodeInterface;
import roles.LearnerInterface;
import roles.ProposerInterface;
import structs.ConnectionDetails;
import structs.RequestPendingConsensus;

/**
 * @author Victor
 *
 */
@SuppressWarnings("unused") // Remove when done
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
	
	// Node data storage structures
	private static DistributedNode distributedNode;
	private TableUpdateLogger updateLog;
	
	// Network sockets
	private ServerSocket serverSocket; // socket is used for downloading logs and connecting to the server
	private ServerSocket paxosSocket; // socket is used for communicating on the paxos network
	private ServerSocket heartbeatSocket; // socket used to respond to heartbeat reqs

	// Paxos state variables
	private int majority;
	private boolean isPaxosLeader;
	private boolean paxosNetworkIsPaused; // used to stop the paxos network when a leader has died
	private boolean paxosRoundRunning;
	private ConcurrentHashMap<Integer, ConnectionDetails> nodeConnections; // key: remote node id, value: details of connection... is managed by paxos leader
	private Queue<Integer> reusableNodeIds;
	
	// Proposal & Prepare phase structures
	private PriorityQueue<ProposalRequest> proposalsToServe;
	private RequestPendingConsensus<PrepareRequest> reqPendingPromise; // The prepare request that is waiting to be promised

	// Promise phase structures
	private PrepareRequest lastPromisedPrepareRequest;
	
	// Accept & accepted phase structures
	private RequestPendingConsensus<AcceptRequest> reqPendingAcceptance; // The accepted value that is still pending acceptance
	private PriorityQueue<AcceptRequest> abortedAcceptsToAttach;
	private AcceptRequest lastAcceptedRequestNotForgotten; // null if leader acknowledges the accepted, else inconsistent state among nodes
	
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

	public static DistributedNode getInstance() throws IOException {
		if (distributedNode == null) {
			distributedNode = new DistributedNode();
		}
		
		return distributedNode;
	}
	

	@Override
	public boolean joinNetwork(String remoteSocketAddress) {
		String[] vals = remoteSocketAddress.split(":");
		if (vals.length != 2) return false;
		Socket connection = null;
		try {
			connection = new Socket(vals[0].trim(),Integer.parseInt(vals[1].trim()));
			ObjectOutputStream outStream = new ObjectOutputStream(connection.getOutputStream());
			
			JoinNetworkRequest connectReq = new JoinNetworkRequest();
			outStream.writeObject(connectReq);
			outStream.flush();
			
			ObjectInputStream inStream = new ObjectInputStream(connection.getInputStream());
			while (inStream.available() == 0) {
				// Wait for response from connection
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
			
			ConnectionDetails details = new ConnectionDetails(serverIp, serverSocket.getLocalPort(), paxosSocket.getLocalPort(), heartbeatSocket.getLocalPort());
			outStream.writeObject(new NodeIdRequest(details));
			outStream.flush();
			
			// Read packet and double check object class
			while (inStream.available() == 0) {
				// Wait for response from connection
				if(connection.isInputShutdown()) {
					return false;
				}
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
			DistributedTable.getInstance().loadTable(nodeIdRes.tableCopy);
			
			connection.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}


	@Override
	public int electLeader() {
		AtomicInteger numAcks = new AtomicInteger(0);
		List<FutureTask<ElectionResponse>> votes = new ArrayList<>();
		
		for (ConnectionDetails connDetails: this.nodeConnections.values()) {
			FutureTask<ElectionResponse> vote = new FutureTask<ElectionResponse>(new Callable<ElectionResponse>() {
				@Override
				public ElectionResponse call() {
					ElectionResponse res = null;
					
					try {	
						Socket remoteSocket = new Socket(connDetails.serverIp, connDetails.paxosPort);
						ObjectOutputStream out = new ObjectOutputStream(remoteSocket.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(remoteSocket.getInputStream());
						
						if (remoteSocket.isConnected()) {
							out.writeObject(new ElectionRequest(nodeId));
							
							while (in.available() == 0) {
								if (remoteSocket.isInputShutdown()) {
									return res;
								}
							}
							
							Object packet = in.readObject();
							if (packet instanceof ElectionResponse) {
								res = (ElectionResponse) packet;
							}
							remoteSocket.close();
						}
					} catch (Exception e) {
						// Do nothing and skip
					}
					return res;
				}
			});
			
			new Thread(vote).start();
			votes.add(vote);
		}
		
		Integer lowestNodeId = this.nodeId;

		// Figure out the lowest node and make them the leader
		for (FutureTask<ElectionResponse> vote: votes) {
			ElectionResponse res;
			try {
				res = vote.get();
				if (res.remoteLowerNodeId != null && res.remoteLowerNodeId > lowestNodeId) {
					lowestNodeId = res.remoteLowerNodeId;
				}
			} catch (Exception e) {
				continue;
			}
				
		}
		
		if (lowestNodeId == this.nodeId) {
			this.isPaxosLeader = true;
		} else {
			this.isPaxosLeader = false;
		}
		
		ConnectionDetails details = this.nodeConnections.get(nodeId);
		this.paxosLeaderIp = details.serverIp;
		this.paxosLeaderPort = details.paxosPort;
		
		return lowestNodeId;
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
		Thread pulseChecker = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						// Run heartbeat checks every 10 secs and remove any that fail to respond
						Thread.sleep(10000);
						for (Integer key: nodeConnections.keySet()) {
							ConnectionDetails connDetails = nodeConnections.get(key);
							if (connDetails.serverIp == serverIp) {
								continue;
							}
							Socket heartbeatSocket = new Socket(connDetails.serverIp, connDetails.heartbeatPort);
							if (heartbeatSocket.isConnected()) {
								// Do nothing
							} else {
								nodeConnections.remove(key);
								recalculateMajority();
								reusableNodeIds.offer(key);
							}
							
							heartbeatSocket.close();
						}
					}
				} catch (Exception e) {
					terminate();
				}
			}
		});
		
		pulseChecker.start();
	}
	
	@Override
	public void proposeTransaction(AbstractDistributedCommand command) {
		ProposalRequest proposal = new ProposalRequest(this.nodeId, command);
		if (isPaxosLeader) {
			prepareTransaction(proposal);
		} else {
			try {
				sendObjectToLeader(proposal);
			} catch (IOException e) {
				System.out.println("Proposal was not uploaded to leader");
			}			
		}
	}

	
	@Override
	public void prepareTransaction(ProposalRequest proposal) {
		if (paxosRoundRunning) return;
		paxosRoundRunning = true;
		PrepareRequest prepareReq = new PrepareRequest(proposal.proposalId);
		reqPendingPromise = new RequestPendingConsensus<PrepareRequest>(prepareReq, this.majority);
		
		// Propose value for all acceptors and wait for ack/nack
		for (ConnectionDetails connDetails: this.nodeConnections.values()) {
			Thread prepareThread = new Thread(new Runnable() {
				public void run() {
					try {
						sendObjectToAddress(prepareReq, connDetails.serverIp, connDetails.paxosPort);
					} catch (Exception e) {
						// Stop operation. The heartbeat should take care of socket severance
						reqPendingPromise.incrementNacks();
					}
				} 
			});
			
			prepareThread.start();
		}
	}
	
	@Override
	public void promiseTransaction(PrepareRequest prepareReq) {
		PromiseResponse promise;
		if (lastPromisedPrepareRequest != null && lastPromisedPrepareRequest.proposalId.timestamp.compareTo(prepareReq.proposalId.timestamp) >= 0) {
			promise = new PromiseResponse(prepareReq.proposalId, false, null);
		} else if (reqPendingAcceptance != null) {
			promise = new PromiseResponse(prepareReq.proposalId, true, reqPendingAcceptance.getRequest());
		} else {
			lastPromisedPrepareRequest = prepareReq;
			promise = new PromiseResponse(prepareReq.proposalId, true, null);
		}
		
		try {
			sendObjectToLeader(promise);
		} catch (Exception e) {
			System.out.println("Promise failed to send");
		}
	}

	@Override
	public void requestAccept(PromiseResponse promiseRes) {
		if (reqPendingPromise.isPending() && promiseRes.proposalId.equals(reqPendingPromise.getRequest().proposalId)) {
			if (promiseRes.ack) { 
				reqPendingPromise.incrementAcks();
			} else if (promiseRes.abortedAcceptRequest != null) {
				abortedAcceptsToAttach.add(promiseRes.abortedAcceptRequest);
				reqPendingPromise.incrementAcks();
			} else {
				reqPendingPromise.incrementNacks();
				//TODO: Implement I'm not the leader protocol
			}
		}
		
		if (reqPendingPromise.isAccepted()) {
			if (!proposalsToServe.isEmpty()) {
				AcceptRequest acceptReq;
				if (!abortedAcceptsToAttach.isEmpty()) {
					acceptReq = abortedAcceptsToAttach.poll();
				} else {
					ProposalRequest proposalReq = proposalsToServe.poll();
					acceptReq = new AcceptRequest(proposalReq.proposalId, proposalReq.command);	
				}

				reqPendingAcceptance = new RequestPendingConsensus<AcceptRequest>(acceptReq, majority);
				for (ConnectionDetails connDetails: this.nodeConnections.values()) {
					new Thread(new Runnable() {
						public void run() {
							try {
								sendObjectToAddress(acceptReq, connDetails.serverIp, connDetails.serverPort);
							} catch (Exception e) {
								// Stop operation because the heartbeat should take care of socket severance							
							}
						} 
					}).start();
				}
			}
		}
	}

	@Override
	public void acceptProposal(AcceptRequest acceptReq) {
		lastAcceptedRequestNotForgotten = reqPendingAcceptance.getRequest();
		updateLog.append(acceptReq);

		try {
			sendObjectToLeader(new AcceptedResponse());
			lastAcceptedRequestNotForgotten = null;
		} catch (Exception e) {
						
		}; 
	}
	
	public void finalizeProposalAcceptance() {
		if (reqPendingAcceptance.isAccepted()) {
			updateLog.append(reqPendingAcceptance.getRequest());
		}
		if (!proposalsToServe.isEmpty()) {
			requestAccept(null);
		}
	}

	@Override
	public void terminate() {
		System.exit(0);
	}
	
	public ServerSocket getServerSocket() {
		return this.serverSocket;
	}
	
	public ServerSocket getPaxosSocket() {
		return this.paxosSocket;
	}
	
	private boolean startServer() {
		Thread server = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Socket clientSocket = serverSocket.accept();
						ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
						ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
						
						while (in.available() != 0 || !clientSocket.isInputShutdown()) { 
							Object packet = in.readObject();
							
							if (packet instanceof JoinNetworkRequest) {
								JoinNetworkResponse res = new JoinNetworkResponse(paxosLeaderIp, paxosLeaderPort);
								out.writeObject(res);
							} else if (isPaxosLeader) {
								if (packet instanceof NodeIdRequest) {
									NodeIdRequest req = (NodeIdRequest) packet;
									int nodeId = getNodeId();
									NodeIdResponse res;
									if (nodeId == -1) {
										res = new NodeIdResponse(nodeId, null, null);
									} else {
										nodeConnections.put(nodeId, req.connDetails);
										res = new NodeIdResponse(nodeId, nodeConnections, DistributedTable.getInstance().copyTable());
									}
									out.writeObject(res);
									
								}
							} 
						}
					}
				} catch (Exception e) {
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
						Object packet = in.readObject();
						
						if (packet instanceof PrepareRequest) {
							promiseTransaction((PrepareRequest) packet);
						} else if (packet instanceof AcceptRequest) {
							AcceptRequest acceptReq = (AcceptRequest) packet;
							if (reqPendingAcceptance == null) {
								reqPendingAcceptance = new RequestPendingConsensus<>(acceptReq, majority);
							}
							if (reqPendingAcceptance.requestsEqual(acceptReq)) {
								reqPendingAcceptance.incrementAcks();
								if (reqPendingAcceptance.isAccepted()) {
									reqPendingAcceptance = null;
									acceptProposal(acceptReq);
								}
							} 
						} else if (packet instanceof ElectionRequest) {
							
						} else if (isPaxosLeader) {
							if (packet instanceof ProposalRequest) {
								proposalsToServe.add((ProposalRequest) packet);
								prepareTransaction((ProposalRequest) packet);
							} else if (packet instanceof PromiseResponse) {
								requestAccept((PromiseResponse) packet);
							} else if (packet instanceof AcceptedResponse) {
								finalizeProposalAcceptance();
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
	
	private void sendObjectToLeader(Object packet) throws UnknownHostException, IOException {
		sendObjectToAddress(packet, paxosLeaderIp, paxosLeaderPort);
	}
	
	private void sendObjectToAddress(Object packet, String ipAddress, int port) throws UnknownHostException, IOException {
		Socket socket = new Socket(ipAddress, port);
		ObjectOutputStream out = null;
		if (socket.isConnected()) {
			out = new ObjectOutputStream(socket.getOutputStream());
		} else {
			socket.close();
			throw new IOException();
		}
		out.writeObject(packet);
		out.flush();
		socket.close();
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
	
	private int getNodeId() {
		int nodeId = -1; 
		if (reusableNodeIds.isEmpty()) {
			nodeId = nodeConnections.keySet().stream().reduce(-1, (c1, c2) -> Math.max(c1, c2))+1;
		} else {
			nodeId = reusableNodeIds.poll();
		}
		
		return nodeId;
	}
}
