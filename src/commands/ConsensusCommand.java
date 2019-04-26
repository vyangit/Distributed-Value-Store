package commands;

import java.util.HashSet;

import enums.CommandPrefix;
import exceptions.InvalidCommandArgumentException;

public abstract class ConsensusCommand extends Command{

	public final boolean needsConsensus = true;
	
	protected ConsensusCommand(CommandPrefix commandType, String usageInstructions, HashSet<String> args)
			throws InvalidCommandArgumentException {
		super(commandType, usageInstructions, args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
