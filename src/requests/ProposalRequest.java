package requests;

import commands.Command;
import structs.ProposalId;

public class ProposalRequest {
	public ProposalId proposalId;
	public Command[] cmds;
	
	public ProposalRequest (int nodeId, Command[] cmds) {
		this.proposalId = new ProposalId(nodeId);
		this.cmds = cmds;
	}
}
