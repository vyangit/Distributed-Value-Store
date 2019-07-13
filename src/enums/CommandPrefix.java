package enums;

public enum CommandPrefix {
	// Local commands
	COPY, // <file_name> <dest_folder_path>
	HELP, //
	
	// Distributed commands
	GET, // <key>
	PUT, // <key> <value>
	DELETE, // <key>
	PROCESS, // <file_name>
	EXIT
}
