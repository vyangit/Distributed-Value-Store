package requests;

public class JoinNetworkRequest {
	int paxosPort;
	String serverIp;
	
	public JoinNetworkRequest(String serverIp, int paxosPort) {
		this.serverIp = serverIp; 
		this.paxosPort = paxosPort;
	}
}
