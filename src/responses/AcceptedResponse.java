package responses;

import commands.Command;
import structs.ProposalId;

public class AcceptedResponse {
	public ProposalId proposalId;
	public Command[] cmds;
	
	public AcceptedResponse(ProposalId proposalId, Command[] cmds) {
		this.cmds = cmds;
	}
}
