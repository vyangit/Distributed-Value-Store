package commands;

import exceptions.InvalidCommandArgumentException;
import exceptions.InvalidCommandException;

public class CommandManager {
	public static void processCommand(String cmd) {
		Command command = null;
		
		try {
			command = CommandParser.parseCommand(cmd);
		} catch (InvalidCommandArgumentException e) {
			
		} catch ( InvalidCommandException e) {
			
		}
		
		if (command == null) {
			System.out.println("An unknown error has occurred when processing the command");
			return;
		}
		
		switch(command.commandType) {
			case GET: processGet(command);
			case PUT: processPut(command); 
			case DELETE: processDelete(command);
			case COPY: processCopy(); 
			case HELP: getHelp(); 
		}
	}

	private static void processGet(Command command) {
		// TODO Auto-generated method stub
		
	}

	private static void processPut(Command command) {
		// TODO Auto-generated method stub
		
	}
	
	private static void processDelete(Command command) {
		// TODO Auto-generated method stub
		
	}
	
	private static void processCopy() {
		// TODO Auto-generated method stub
		
	}
	
	private static void getHelp() {
		// TODO Auto-generated method stub
		
	}

}
