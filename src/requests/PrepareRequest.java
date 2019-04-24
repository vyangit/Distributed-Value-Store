package requests;

import structs.ProposalId;

/**
 * @author Victor
 * 
 * Information about the proposal sent out to nodes
 */
public class PrepareRequest {
	public ProposalId proposalId;
	
	public PrepareRequest (ProposalId proposalId) {
		this.proposalId = proposalId;
	}
}
