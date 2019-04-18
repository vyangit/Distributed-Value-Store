package roles;

public interface DistributedNodeInterface {
	
	/**
	 * Requests to join a network when given a node in that node work
	 * @return 
	 */
	public boolean joinNetwork(String remoteSocketAddress);

	/**
	 * Establishes node's heartbeat 
	 */
	public boolean establishHeartbeat();
	
	/**
	 * Terminates the node 
	 */
	public void terminate();
	
	
	
}
