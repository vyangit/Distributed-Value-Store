package structs;

import java.util.Date;
import java.util.UUID;

/**
 * @author Victor
 * 
 * Information about the proposal sent out to nodes
 */
public class ProposalRequest {
	UUID id;
	Date timestamp; // used with uuid to identify variations
	String key;
	String value;
	
	public ProposalRequest (String key, String value) {
		this.id = UUID.randomUUID();
		this.timestamp = new Date();
		this.key = key; 
		this.value = value; 
	}
}
