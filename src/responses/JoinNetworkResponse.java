package responses;

public class JoinNetworkResponse implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1688870802348838807L;
	public String paxosLeaderIp;
	public int paxosLeaderPort;
	
	public JoinNetworkResponse(String paxosLeaderIp, int paxosLeaderPort) {
		this.paxosLeaderIp = paxosLeaderIp;
		this.paxosLeaderPort = paxosLeaderPort;
	}
}
