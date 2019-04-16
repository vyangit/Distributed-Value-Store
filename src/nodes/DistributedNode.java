package nodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import exceptions.UnidentifiedIpException;
import roles.AcceptorsInterface;
import roles.DistributedNodeInterface;
import roles.LearnerInterface;
import roles.ProposerInterface;
import structs.ConnectionDetails;
import structs.IpPort;
import structs.Promise;
import structs.Proposal;

/**
 * @author Victor
 *
 */
public class DistributedNode implements DistributedNodeInterface, AcceptorsInterface, LearnerInterface, ProposerInterface {
	// Use a hashtable heartbeat access and lookup access may collide
	
	private DistributedNode distributedNode;
	public String serverIp;
	private ServerSocket serverSocket;
	private ServerSocket paxosSocket;
	private List<String> commandLog;
	private ConcurrentHashMap<IpPort, ConnectionDetails> ipConnections; // key: remote ip address, value: details of connection
	
	public DistributedNode() throws IOException {
		this.serverSocket = new ServerSocket(0);
		this.paxosSocket = new ServerSocket(0);
		this.serverIp = getOutboundServerAddress();
		this.commandLog = Collections.synchronizedList(new ArrayList<String>());
		this.ipConnections = new ConcurrentHashMap<IpPort, ConnectionDetails>();
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

	@Override
	public void joinNetwork(String remoteIpAddress) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void checkHeartbeat(Socket remoteConnection) {
		// TODO Auto-generated method stub
		
	}
	
	
	public DistributedNode getInstance() throws IOException {
		if (this.distributedNode == null) {
			this.distributedNode = new DistributedNode();
		}
		
		return this.distributedNode;
	}
	
	/**
	 * @param socketAddress
	 * @return True if successful or false if unsucessful add
	 * @throws UnidentifiedIpException
	 */
	public boolean addRemoteConnection(String socketAddress) {
		ConnectionDetails connDetails;
		try {
			connDetails = new ConnectionDetails(socketAddress);
		} catch (UnidentifiedIpException e) {
			return false;
		}
		
		this.ipConnections.put(connDetails.ipPort, connDetails);
		return true;
		
	}
	
	public ServerSocket getServerSocket() {
		return this.serverSocket;
	}
	
	public ServerSocket getPaxosSocket() {
		return this.paxosSocket;
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
