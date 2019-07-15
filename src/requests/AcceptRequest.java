package requests;

import commands.AbstractDistributedCommand;
import structs.ProposalId;

public class AcceptRequest implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 428408137860881946L;
	public ProposalId proposalId;
	public AbstractDistributedCommand command;
	
	public AcceptRequest(ProposalId proposalId, AbstractDistributedCommand command) {
		this.proposalId = proposalId;
		this.command = command;
	}
}
