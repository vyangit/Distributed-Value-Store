package responses;

import requests.AcceptRequest;
import structs.ProposalId;

public class PromiseResponse implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 595920814735571819L;

	public ProposalId proposalId;
	public boolean ack;
	public AcceptRequest abortedAcceptRequest;
	
	public PromiseResponse(ProposalId proposalId, boolean ack, AcceptRequest abortedAcceptRequest) {
		this.proposalId = proposalId;
		this.ack = ack;
		this.abortedAcceptRequest = abortedAcceptRequest; // This request is in the case a request was not fully broadcasted from the last dead paxos leader
	}
}
