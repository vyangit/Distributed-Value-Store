package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class CopyCommand extends Command{
	public CopyCommand() throws InvalidCommandArgumentException {
		super(CommandPrefix.COPY, null, null, "copy", null);
	}

	@Override
	public String toString() {
		return String.format("%s", commandType.toString());
	}
}
