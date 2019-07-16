package requests;

import structs.ProposalId;

/**
 * @author Victor
 * 
 * Information about the proposal sent out to nodes
 */
public class PrepareRequest implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 222863686802611003L;
	public ProposalId proposalId;
	
	public PrepareRequest(ProposalId proposalId) {
		this.proposalId = proposalId;
	}
}
