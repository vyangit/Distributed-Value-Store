package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class PutCommand extends AbstractDistributedCommand{
	public final String key;
	public final String value;
	
	public PutCommand(String key, String value) throws InvalidCommandArgumentException {
		super(CommandPrefix.DELETE, "put <key> <value>", null);
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("%s key:%s value:%s", commandType.toString(), this.key, this.value);
	}
}
