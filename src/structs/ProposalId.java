package structs;

import java.util.Date;
import java.util.UUID;

public class ProposalId {
	public UUID id;
	public int nodeId;
	public Date timestamp;
	
	public ProposalId(int nodeId) {
		this.id = UUID.randomUUID();
		this.nodeId = nodeId;
		this.timestamp = new Date();
	}
	
	@Override
	public String toString() {
		return String.format("%s; proposalId:%s; nodeId:%s", DateParser.parseLogDate(new Date()), id.toString(), Integer.toString(nodeId));
	}
}
