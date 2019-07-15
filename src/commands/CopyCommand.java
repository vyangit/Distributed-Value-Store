package commands;

import java.io.File;
import java.io.IOException;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public class CopyCommand extends AbstractLocalCommand{
	
	public final String fileName;
	public final String destFileDir;
	public final String completePath;
	
	public CopyCommand(String fileName, String destFileDir) throws InvalidCommandArgumentException {
		super(CommandPrefix.COPY, "copy", null);
		this.destFileDir = destFileDir;
		this.fileName = fileName;
		File dir = new File(destFileDir);
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
