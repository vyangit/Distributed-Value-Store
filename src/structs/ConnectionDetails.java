package structs;

import java.net.Socket;

import exceptions.UnidentifiedIpException;

public class ConnectionDetails {

	public IpPort ipPort; 
	public Socket heartbeat; //Socket connection to the heartbeat
	
	public ConnectionDetails(String remoteSocketAddress) throws UnidentifiedIpException {
		this.ipPort = new IpPort(remoteSocketAddress);
		this.heartbeat = initHeartbeat(remoteSocketAddress);
	}
	
	private Socket initHeartbeat(String remoteSocketAddress) {
		//TODO: Add threading and socket establishment
		return new Socket();
	}
}
