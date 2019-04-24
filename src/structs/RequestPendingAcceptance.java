package structs;

import requests.AcceptRequest;

public class RequestPendingAcceptance {
	private AcceptRequest acceptRequest;
	private int nacks;
	private int acks;
	private int majority;
	
	public RequestPendingAcceptance(AcceptRequest acceptRequest, int majority) {
		this.acceptRequest = acceptRequest;
		this.nacks = 0;
		this.acks = 0;
		this.majority = majority;
	}
	
	public void incrementAcks() {
		acks++;
	}
	
	public void incrementNacks() {
		nacks++;
	}
	
	public int getMajority() {
		return majority;
	}
	
	public boolean requestsEqual(AcceptRequest acceptRequest) {
		return this.acceptRequest.equals(acceptRequest);
	}
	
	public Boolean checkIfAccepted() {
		if (acks >= majority) return true;
		if (nacks >= majority) return false;
		return null;
	}
}
