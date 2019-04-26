package commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import exceptions.InvalidCommandArgumentException;
import exceptions.InvalidCommandException;
import nodes.DistributedNode;
import nodes.DistributedTable;
import utils.CommandFileParser;

public class CommandManager {
	public static void processCommand(String cmd) {
		Command command = null;
		
		try {
			command = CommandParser.parseCommand(cmd);
			if (command == null) {
				System.out.println("An unknown error has occurred when processing the command");
				return;
			}
			
			switch(command.commandType) {
				case GET: processGet(command);
				case PUT: processNodeCommands(command); 
				case PROCESS: processFile(command); 
				case DELETE: processNodeCommands(command);
				case COPY: processCopy(command); 
				case HELP: getHelp(); 
			}
		} catch (InvalidCommandArgumentException e) {
			System.out.println("Invalid command");
		} catch (InvalidCommandException e) {
			System.out.println("Invalid command parameter");			
		}
	}

	private static void processGet(Command command) {
		System.out.println(DistributedTable.getInstance().getValue(command.key));
	}

	private static void processNodeCommands(Command command) {
		Command[] cmds = new Command[1];
		cmds[0] = command;
		try {
			DistributedNode.getInstance().proposeTransaction(cmds);
		} catch (IOException e) {
			System.out.println("Node error");
		}
	}
	
	private static void processNodeCommands(Command[] commands) {
		try {
			DistributedNode.getInstance().proposeTransaction(commands);
		} catch (IOException e) {
			System.out.println("Node error");
		}
	}
	
	private static void processFile(Command command) throws InvalidCommandArgumentException, InvalidCommandException {
		ProcessFileCommand fileCmd = (ProcessFileCommand) command;
		try {
			processNodeCommands(CommandFileParser.parseCommandFile(fileCmd.filePath));
		} catch (IOException e) {
			System.out.println("Invalid file commands found");
		}
	}
	
	private static void processCopy(Command command) {
		CopyCommand copyCmd = (CopyCommand) command;
		File copyFile = new File(copyCmd.filePath);
		try {
			if (copyFile.exists()) {
				System.out.println("File already exists. Process aborted to prevent override");
			}
			copyFile.createNewFile();
		} catch (IOException e1) {
			System.out.println("File cannot be created in this folder");
		}
		
		try {
			BufferedWriter copyStream = new BufferedWriter(new FileWriter(copyFile));
			for (String entry: DistributedTable.getInstance().getTableAsList()) {
				copyStream.write(entry);
			}
			copyStream.close();
			
		} catch (IOException e) {
			System.out.println("File copying encountered an error. Rolling back copy.");
		}
	}
	
	private static void getHelp() {
		//TODO: write help manual
		System.out.println("Help wanted");
		
	}
}
