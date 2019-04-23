package commands;

import enums.CommandPrefix;

public class DeleteCommand extends Command{

	public DeleteCommand(String key) {
		super(CommandPrefix.DELETE, key, null);
	}

	@Override
	public String toString() {
		return String.format("%s key:%s", commandType.toString(), this.key);
	}

}
