package commands;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class CopyCommand extends AbstractLocalCommand{
	
	public String destFilePath;
	
	public CopyCommand(String destFilePath) throws InvalidCommandArgumentException {
		super(CommandPrefix.COPY, "copy", null);
		this.destFilePath = destFilePath;
	}

	@Override
	public String toString() {
		return String.format("%s", commandType.toString());
	}
}
