package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import commands.Command;
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
	public static Command[] parseCommandFile(String filePath) throws IOException, InvalidCommandArgumentException, InvalidCommandException {
		File cmdFile = new File(filePath);
		if (cmdFile.exists() || cmdFile.canRead()) {
			throw new IOException("Invalid file");
		}

		BufferedReader fileIn = new BufferedReader(new FileReader(cmdFile));
		List<Command> parsedCmds = new ArrayList<Command>();
		
		while (fileIn.ready()) {
			parsedCmds.add(CommandParser.parseCommand(fileIn.readLine()));
		}
		fileIn.close();
		return parsedCmds.toArray(new Command[parsedCmds.size()]);
	}
}
