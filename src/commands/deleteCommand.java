package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class DeleteCommand extends AbstractDistributedCommand{
	public final String key;
	
	public DeleteCommand(String key) throws InvalidCommandArgumentException {
		super(CommandPrefix.DELETE, "delete <key>", null);
		this.key = key;
	}

	@Override
	public String toString() {
		return String.format("%s key:%s", commandType.toString(), this.key);
	}

}
