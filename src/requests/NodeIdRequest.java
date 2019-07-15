package requests;

import structs.ConnectionDetails;

public class NodeIdRequest implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5994205957044400555L;
	public ConnectionDetails connDetails;
	
	public NodeIdRequest(ConnectionDetails connDetails) {
		this.connDetails = connDetails;
	}
}
