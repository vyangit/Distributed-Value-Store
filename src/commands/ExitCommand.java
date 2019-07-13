package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class ExitCommand extends AbstractLocalCommand{
	public ExitCommand() throws InvalidCommandArgumentException {
		super(CommandPrefix.EXIT, "help", null);
	}

	@Override
	public String toString() {
		return String.format("%s", commandType.toString());
	}
}
