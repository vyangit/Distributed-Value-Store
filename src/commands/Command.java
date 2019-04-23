package commands;

import enums.CommandPrefix;

public abstract class Command {
	public CommandPrefix commandType;
	public String key;
	public String value;
	
	protected Command(CommandPrefix commandType, String key, String value) {
		this.commandType = commandType;
		this.key = key;
		this.value = value;
	}
	
	public String toString() {
		return String.format("%s key:%s val:%s", commandType.toString(), this.key, this.value);
	}
}
