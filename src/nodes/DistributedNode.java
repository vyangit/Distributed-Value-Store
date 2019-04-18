package nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import roles.AcceptorsInterface;
import roles.DistributedNodeInterface;
import roles.LearnerInterface;
import roles.ProposerInterface;
import structs.ConnectionDetails;
import structs.JoinNetworkRequest;
import structs.JoinNetworkResponse;
import structs.NodeIdRequest;
import structs.NodeIdResponse;
import structs.Promise;
import structs.Proposal;

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
	
	private boolean isPaxosLeader;
	private DistributedNode distributedNode;
	private ServerSocket serverSocket; // socket is used for downloading logs and connecting to the server
	private ServerSocket paxosSocket; // socket is used for communicating on the paxos network
	private ServerSocket heartbeatSocket; // socket used to respond to heartbeat reqs
	private TableUpdateLogger updateLog;
	
	private ConcurrentHashMap<Integer, ConnectionDetails> nodeConnections; // key: remote ip address, value: details of connection... is managed by paxos leader
	private Hashtable<Integer, Socket> heartbeatConnections;
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
		
		establishHeartbeat();
		
		this.serverIp = getOutboundServerAddress();
		this.updateLog = new TableUpdateLogger();
		this.nodeConnections = new ConcurrentHashMap<Integer, ConnectionDetails>();
		this.heartbeatConnections = new Hashtable<Integer, Socket>();
		this.reusableNodeIds = new LinkedList<Integer>();
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
	public boolean establishHeartbeat() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						Socket clientSocket = heartbeatSocket.accept();
						while (!clientSocket.isInputShutdown()) { 
							// wait until disconnection
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return true;
	}
	
	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		System.exit(1);
	}
	
	@Override
	public void proposeTransaction(Proposal proposal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestAccept() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void promiseTransaction(Promise promise) {
		// TODO Auto-generated method stub
		
	}

	
	public DistributedNode getInstance() throws IOException {
		if (this.distributedNode == null) {
			this.distributedNode = new DistributedNode();
		}
		
		return this.distributedNode;
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
