package commands;

import java.util.HashSet;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

/**
 * @author Victor
 * Mapping for console commands 
 */
public abstract class Command {
	public final CommandPrefix commandType;
	public final String key;
	public final String value;
	public HashSet<String> args;
	private HashSet<String> validArgs;
	private final String usageInstructions;
	
	
	protected Command(CommandPrefix commandType, String key, String value, String usageInstructions, HashSet<String> args) throws InvalidCommandArgumentException {
		this.commandType = commandType;
		this.key = key;
		this.value = value;
		this.usageInstructions = usageInstructions;
		this.args = args;
		this.validArgs = new HashSet<String>();
		
		String invalidArg = findFirstInvalidArg();
		if (!invalidArg.isEmpty()) {
			throw new InvalidCommandArgumentException(invalidArg);
		}
	}
	
	public String toString() {
		return String.format("%s key:%s val:%s", commandType.toString(), this.key, this.value);
	}
	
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
