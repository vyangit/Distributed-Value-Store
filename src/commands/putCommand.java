package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class PutCommand extends Command{
	public PutCommand(String key, String val) throws InvalidCommandArgumentException {
		super(CommandPrefix.DELETE, key, val, "put <key> <value>", null);
	}

	@Override
	public String toString() {
		return String.format("%s key:%s value:%s", commandType.toString(), this.key, this.value);
	}
}
