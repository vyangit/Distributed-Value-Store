package responses;

public class ElectionResponse {
	public boolean ack;
	public Integer remoteLowerNodeId;
	
	public ElectionResponse(boolean ack, Integer remoteLowerNodeId) {
		this.ack = ack;
		this.remoteLowerNodeId = remoteLowerNodeId;
	}
}
