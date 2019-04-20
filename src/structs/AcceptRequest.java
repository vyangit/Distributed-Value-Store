package structs;

import java.util.Date;
import java.util.UUID;

public class AcceptRequest {
	UUID id;
	Date timestamp;
	String[] cmds;
	
	public AcceptRequest(String[] cmds) {
		this.cmds = cmds;
	}
}
