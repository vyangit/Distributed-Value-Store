package commands;

import java.io.IOException;
import java.util.HashSet;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;
import exceptions.InvalidCommandException;
import utils.CommandFileParser;

public class ProcessFileCommand extends Command{

	public String filePath;
	public Command[] commands;
	
	protected ProcessFileCommand(CommandPrefix commandType, String usageInstructions, HashSet<String> args, String filePath) throws InvalidCommandArgumentException, IOException, InvalidCommandException {
		super(commandType, usageInstructions, args);
		this.commands = CommandFileParser.parseCommandFile(filePath);
	}
	
	@Override
	public String toString() {
		return String.format("process %s", filePath);
	}
}
