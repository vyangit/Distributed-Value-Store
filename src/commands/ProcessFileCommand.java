package commands;

import java.io.IOException;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;
import exceptions.InvalidCommandException;
import utils.CommandFileParser;

public class ProcessFileCommand extends AbstractDistributedCommand{

	public final String filePath;
	public final AbstractCommand[] commands;
	
	protected ProcessFileCommand(String filePath) throws InvalidCommandArgumentException, IOException, InvalidCommandException {
		super(CommandPrefix.PROCESS, "process <file_name>", null);
		this.filePath = filePath;
		this.commands = CommandFileParser.parseCommandFile(filePath);
	}
	
	@Override
	public String toString() {
		return String.format("process %s", filePath);
	}
}
