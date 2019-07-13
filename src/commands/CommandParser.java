package commands;

import exceptions.InvalidCommandArgumentException;
import exceptions.InvalidCommandException;

public class CommandParser {
	public static AbstractCommand parseCommand(String commandString) {
		try {
			String[] args = commandString.split(" ");
			String prefix = args[0];
			if (prefix.equals("get") && args.length == 2) {
				return new GetCommand(args[1]);
			} else if (prefix.equals("put") && args.length == 3) {
				return new PutCommand(args[1],args[2]);
			} else if (prefix.equals("delete") && args.length == 2) {
				return new DeleteCommand(args[1]);
			} else if (prefix.equals("process") && args.length == 2) {
				return new ProcessFileCommand(args[1]);
			} else if (prefix.equals("copy") && args.length == 3) {
				return new CopyCommand(args[1], args[2]);
			}  else if (prefix.equals("help") && args.length == 1) {
				return new HelpCommand();
			} else if (prefix.equals("exit")) {
				return new ExitCommand();
			}
		} catch (InvalidCommandArgumentException e) {
			System.out.println("Invalid command");
		} catch (InvalidCommandException e) {
			System.out.println("Invalid command parameter");			
		} catch (Exception e) {
			String msg = e.getMessage() == null ? "An unknown error has occurred when processing the command" : e.getMessage();
			System.out.println(msg);
		}
		
		return null;
	}
}
