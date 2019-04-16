package roles;

import java.net.Socket;

public interface DistributedNodeInterface {
	
	/**
	 * Requests to join a network when given a node in that node work
	 */
	public void joinNetwork(String remoteIpAddress);

	/**
	 * Checks the heartbeat connection of a given node in the network
	 */
	public void checkHeartbeat(Socket remoteConnection);
	
}
