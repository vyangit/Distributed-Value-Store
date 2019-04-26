package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class GetCommand extends Command{
	public GetCommand(String key, String val) throws InvalidCommandArgumentException {
		super(CommandPrefix.DELETE, key, val, "get <key>", null);
	}

	@Override
	public String toString() {
		return String.format("%s key:%s", commandType.toString(), this.key);
	}
}
