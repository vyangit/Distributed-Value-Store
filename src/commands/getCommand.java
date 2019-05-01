package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class GetCommand extends AbstractCommand{
	public final String key;
	
	public GetCommand(String key) throws InvalidCommandArgumentException {
		super(CommandPrefix.DELETE,"get <key>", null);
		this.key = key;
	}

	@Override
	public String toString() {
		return String.format("%s key:%s", commandType.toString(), this.key);
	}
}
