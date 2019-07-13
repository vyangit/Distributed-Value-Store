package commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import nodes.DistributedNode;
import nodes.DistributedTable;

public class CommandManager {
	public static boolean processCommand(String cmd) {
		AbstractCommand command = null;
		
		command = CommandParser.parseCommand(cmd);
		if (command == null) {
			return false;
		}
		
		switch(command.commandType) {
			case GET: 
				processGet((GetCommand) command); 
				break;
			case COPY: 
				processCopy((CopyCommand) command); 
				break;
			case HELP: 
				processHelp((HelpCommand) command); 
				break;
			case PROCESS:
			case PUT:
			case DELETE: 
				processNodeCommand((AbstractDistributedCommand) command);
				break;
			case EXIT:
				return true;
			default: 
				return false;
		}
		return false;
	}

	private static void processGet(GetCommand command) {
		System.out.println(DistributedTable.getInstance().getValue(command.key));
	}

	private static void processNodeCommand(AbstractDistributedCommand command) {
		try {
			DistributedNode.getInstance().proposeTransaction(command);
		} catch (IOException e) {
			System.out.println("Node error");
		}
	}
	
	private static void processCopy(CopyCommand command) {
		File copyFile = new File(command.destFilePath);
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
	
	private static void processHelp(HelpCommand command) {
		//TODO: write help manual
		System.out.println("Help wanted");
		
	}
}
