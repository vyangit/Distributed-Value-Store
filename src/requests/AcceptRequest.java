package requests;

import commands.Command;
import structs.ProposalId;

public class AcceptRequest {
	public ProposalId proposalId;
	public Command[] cmds;
	
	public AcceptRequest(ProposalId proposalId, Command[] cmds) {
		this.proposalId = proposalId;
		this.cmds = cmds;
	}
}
