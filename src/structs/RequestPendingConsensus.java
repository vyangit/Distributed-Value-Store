package structs;

public class RequestPendingConsensus<T> {
	private T request;
	private int nacks;
	private int acks;
	private int majority;
	
	public RequestPendingConsensus(T request, int majority) {
		this.request = request;
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
	
	public T getRequest() {
		return request;
	}
	
	
	public boolean requestsEqual(T request) {
		return this.request.equals(request);
	}
	
	public Boolean checkIfAccepted() {
		if (acks >= majority) return true;
		if (nacks >= majority) return false;
		return null;
	}
}
