package requests;

import commands.Command;
import structs.ProposalId;

public class ProposalRequest {
	public ProposalId proposalId;
	public Command[] cmds;
	
	public ProposalRequest (Command[] cmds) {
		this.proposalId = new ProposalId();
		this.cmds = cmds;
	}
}
