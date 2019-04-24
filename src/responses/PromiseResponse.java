package responses;

import requests.AcceptRequest;

public class PromiseResponse {
	public boolean ack;
	public AcceptRequest abortedAcceptRequest;
	
	public PromiseResponse(boolean ack, AcceptRequest abortedAcceptRequest) {
		this.ack = ack;
		this.abortedAcceptRequest = abortedAcceptRequest;
	}
}
