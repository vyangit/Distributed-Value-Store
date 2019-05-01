package commands;

import java.util.HashSet;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

/**
 * @author Victor
 * Mapping for console commands 
 */
public abstract class AbstractCommand {
	public final CommandPrefix commandType;
	public HashSet<String> args;
	private HashSet<String> validArgs;
	private final String usageInstructions;
	

	protected AbstractCommand(CommandPrefix commandType, String usageInstructions, HashSet<String> args) throws InvalidCommandArgumentException {
		this.commandType = commandType;
		this.usageInstructions = usageInstructions;
		this.args = args;
		
		String invalidArg = findFirstInvalidArg();
		if (!invalidArg.isEmpty()) {
			throw new InvalidCommandArgumentException(invalidArg);
		}
	}
	
	public abstract String toString();
	
	public String getUsage() {
		return String.format("Usage: %s", usageInstructions);
	}

	private String findFirstInvalidArg() {
		if (args != null) {
			for (String arg: args) {
				if (!validArgs.contains(arg)) {
					return arg;
				}
			}
		}
		return null;
	}
}
