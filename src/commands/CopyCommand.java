package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class CopyCommand extends Command{
	
	public String filePath;
	
	public CopyCommand(String filePath) throws InvalidCommandArgumentException {
		super(CommandPrefix.COPY, null, null, "copy", null);
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return String.format("%s", commandType.toString());
	}
}
