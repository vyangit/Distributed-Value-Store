package structs;

import exceptions.UnidentifiedIpException;

public class ConnectionDetails {

	public String serverIp; 
	public int serverPort;
	public int paxosPort;
	public int heartbeatPort;
	
	public ConnectionDetails(String serverIp, int serverPort, int paxosPort, int heartbeatPort) throws UnidentifiedIpException {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.paxosPort = paxosPort;
		this.heartbeatPort = heartbeatPort;
	}
}
