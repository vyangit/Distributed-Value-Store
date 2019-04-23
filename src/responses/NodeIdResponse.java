package responses;

import java.util.Map;

import structs.ConnectionDetails;

public class NodeIdResponse {
	public int nodeId;
	public Map<Integer, ConnectionDetails> remoteConnections;
	
	public NodeIdResponse(int nodeId, Map<Integer, ConnectionDetails> remoteConnections) {
		this.nodeId = nodeId;
		this.remoteConnections = remoteConnections;
	}
}
