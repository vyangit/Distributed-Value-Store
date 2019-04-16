package structs;

import java.util.Date;
import java.util.UUID;

import enums.ReqType;

/**
 * @author Victor
 * 
 * Information about the proposal sent out to nodes
 */
public class Proposal {
	UUID id;
	String senderSocketAddress; // used with uuid to identify variations
	Date timestamp; // used with uuid to identify variations
	ReqType reqType;
	String key;
	String value;
	
	public Proposal (String senderSocketAddress, ReqType reqType, String key, String value) {
		this.id = UUID.randomUUID();
		this.timestamp = new Date();
		this.senderSocketAddress = senderSocketAddress;
		this.reqType = reqType;
		this.key = key; 
		this.value = value; 
	}
}
