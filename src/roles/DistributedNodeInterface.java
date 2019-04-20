package roles;

public interface DistributedNodeInterface {
	
	/**
	 * Requests to join a network when given a node in that node work
	 * @return True if successfully joins node network, else false
	 */
	public boolean joinNetwork(String remoteSocketAddress);

	/**
	 * Establishes node's heartbeat 
	 * @return True if heartbeat starts up, else false
	 */
	public boolean establishHeartbeat();
	
	/**
	 * @return Id of the elected leader
	 */
	public int electLeader();
	
	/**
	 * Terminates the node 
	 */
	public void terminate();
	
	
	
}
