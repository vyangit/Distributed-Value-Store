package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class HelpCommand extends Command{
	public HelpCommand() throws InvalidCommandArgumentException {
		super(CommandPrefix.HELP, null, null, "help", null);
	}

	@Override
	public String toString() {
		return String.format("%s", commandType.toString());
	}
}
