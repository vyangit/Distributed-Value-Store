package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class DeleteCommand extends Command{

	public DeleteCommand(String key) throws InvalidCommandArgumentException {
		super(CommandPrefix.DELETE, key, null, "delete <key>", null);
	}

	@Override
	public String toString() {
		return String.format("%s key:%s", commandType.toString(), this.key);
	}

}
