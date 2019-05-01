package commands;

import java.util.HashSet;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public abstract class AbstractLocalCommand extends AbstractCommand {
	protected AbstractLocalCommand(CommandPrefix commandType, String usageInstructions, HashSet<String> args)
			throws InvalidCommandArgumentException {
		super(commandType, usageInstructions, args);
	}
}
