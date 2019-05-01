package commands;

import java.util.HashSet;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public abstract class AbstractDistributedCommand extends AbstractCommand{

	protected AbstractDistributedCommand(CommandPrefix commandType, String usageInstructions, HashSet<String> args)
			throws InvalidCommandArgumentException {
		super(commandType, usageInstructions, args);
	}

}
