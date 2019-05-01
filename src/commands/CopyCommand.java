package commands;

import java.io.File;
import java.io.IOException;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class CopyCommand extends AbstractLocalCommand{
	
	public final String fileName;
	public final String destFilePath;
	public final String completePath;
	
	public CopyCommand(String fileName, String destFilePath) throws InvalidCommandArgumentException {
		super(CommandPrefix.COPY, "copy", null);
		this.destFilePath = destFilePath;
		this.fileName = fileName;
		File dir = new File(destFilePath);
		File file = null;
		String filePath = null;
		
		try {
			if (dir.isDirectory()) {
				file = new File(dir.getCanonicalPath()+"\\"+fileName);
				if (!file.exists()) {
					filePath = file.getCanonicalPath();	
				} else {
					throw new InvalidCommandArgumentException("File already exists in this directory");
				}
			}
		} catch (IOException e) {
			throw new InvalidCommandArgumentException("Invalid directory specified");
		}
		
		completePath = filePath;
	}

	@Override
	public String toString() {
		return String.format("%s", commandType.toString());
	}
}
