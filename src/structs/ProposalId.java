package structs;

import java.util.Date;
import java.util.UUID;

public class ProposalId {
	public UUID id;
	public Date timestamp;
	
	public ProposalId() {
		this.id = UUID.randomUUID();
		this.timestamp = new Date();
	}
}
