package requests;

import commands.AbstractDistributedCommand;
import structs.ProposalId;

public class ProposalRequest implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8600784954973030772L;
	public ProposalId proposalId;
	public AbstractDistributedCommand command;
	
	public ProposalRequest (int nodeId, AbstractDistributedCommand command) {
		this.proposalId = new ProposalId(nodeId);
		this.command = command;
	}
}
