package structs;

import java.util.List;

public class JoinNetworkResponse {
	public String paxosLeaderIp;
	public int paxosLeaderPort;
	public List<String> remoteNodeSockets;
	
	public JoinNetworkResponse(String paxosLeaderIp, int paxosLeaderPort, List<String> remoteNodeSockets) {
		this.paxosLeaderIp = paxosLeaderIp;
		this.paxosLeaderPort = paxosLeaderPort;
	}
}
