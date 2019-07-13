package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import commands.AbstractCommand;
import commands.CommandParser;
import exceptions.InvalidCommandArgumentException;
import exceptions.InvalidCommandException;

public class CommandFileParser {

	/**
	 * @param filePath
	 * @return Array of commands
	 * @throws IOException
	 * @throws InvalidCommandArgumentException
	 * @throws InvalidCommandException
	 */
	public static AbstractCommand[] parseCommandFile(String filePath) throws IOException, InvalidCommandArgumentException, InvalidCommandException {
		File cmdFile = new File(filePath);
		if (!cmdFile.exists() || !cmdFile.canRead()) {
			throw new IOException("Invalid file");
		}

		BufferedReader fileIn = new BufferedReader(new FileReader(cmdFile));
		List<AbstractCommand> parsedCmds = new ArrayList<AbstractCommand>();
		int lineCount = 1;
		
		while (fileIn.ready()) {
			AbstractCommand cmd = CommandParser.parseCommand(fileIn.readLine());
			if (cmd == null) {
				throw new IOException("Invalid file command on line: "+ lineCount);
			}
			lineCount++;
			parsedCmds.add(cmd);
		}
		fileIn.close();
		return parsedCmds.toArray(new AbstractCommand[parsedCmds.size()]);
	}
}
