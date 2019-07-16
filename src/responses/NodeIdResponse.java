package responses;

import java.util.Hashtable;
import java.util.Map;

import structs.ConnectionDetails;

public class NodeIdResponse implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8315097836057423452L;
	public int nodeId;
	public Map<Integer, ConnectionDetails> remoteConnections;
	public Hashtable<String, String> tableCopy;
	
	public NodeIdResponse(int nodeId, Map<Integer, ConnectionDetails> remoteConnections, Hashtable<String,String> tableCopy) {
		this.nodeId = nodeId;
		this.remoteConnections = remoteConnections;
		this.tableCopy = tableCopy;
	}
}
