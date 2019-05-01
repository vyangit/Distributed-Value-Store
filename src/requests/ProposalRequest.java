package requests;

import commands.AbstractDistributedCommand;
import structs.ProposalId;

public class ProposalRequest {
	public ProposalId proposalId;
	public AbstractDistributedCommand command;
	
	public ProposalRequest (int nodeId, AbstractDistributedCommand command) {
		this.proposalId = new ProposalId(nodeId);
		this.command = command;
	}
}
