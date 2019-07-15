package responses;

public class ElectionResponse implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 982764231382141153L;
	public boolean ack;
	public Integer remoteLowerNodeId;
	
	public ElectionResponse(boolean ack, Integer remoteLowerNodeId) {
		this.ack = ack;
		this.remoteLowerNodeId = remoteLowerNodeId;
	}
}
