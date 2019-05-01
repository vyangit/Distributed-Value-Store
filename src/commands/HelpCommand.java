package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class HelpCommand extends AbstractLocalCommand{
	public HelpCommand() throws InvalidCommandArgumentException {
		super(CommandPrefix.HELP, "help", null);
	}

	@Override
	public String toString() {
		return String.format("%s", commandType.toString());
	}
}
