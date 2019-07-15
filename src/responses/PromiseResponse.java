package responses;

import requests.AcceptRequest;

public class PromiseResponse implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 595920814735571819L;
	public boolean ack;
	public AcceptRequest abortedAcceptRequest;
	
	public PromiseResponse(boolean ack, AcceptRequest abortedAcceptRequest) {
		this.ack = ack;
		this.abortedAcceptRequest = abortedAcceptRequest;
	}
}
