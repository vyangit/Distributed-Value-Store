package requests;

import commands.AbstractDistributedCommand;
import structs.ProposalId;

public class AcceptRequest {
	public ProposalId proposalId;
	public AbstractDistributedCommand command;
	
	public AcceptRequest(ProposalId proposalId, AbstractDistributedCommand command) {
		this.proposalId = proposalId;
		this.command = command;
	}
}
