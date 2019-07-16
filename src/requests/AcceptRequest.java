package requests;

import java.util.ArrayList;

import commands.AbstractDistributedCommand;
import responses.AcceptedResponse;
import structs.ProposalId;

public class AcceptRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 428408137860881946L;
	public ProposalId proposalId;
	public AbstractDistributedCommand command;
	public ArrayList<AcceptedResponse> abortedAcceptedRequests;
	
	public AcceptRequest(ProposalId proposalId, AbstractDistributedCommand command) {
		this.proposalId = proposalId;
		this.command = command;
		this.abortedAcceptedRequests = null;
	}
	
	public AcceptRequest(ProposalId proposalId, AbstractDistributedCommand command, ArrayList<AcceptedResponse> abortedAcceptedRequests) {
		this.proposalId = proposalId;
		this.command = command;
		this.abortedAcceptedRequests = abortedAcceptedRequests;
	}
}
