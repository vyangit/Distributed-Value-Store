package nodes;

import java.io.IOException;
import java.util.Hashtable;

import commands.Command;
import enums.CommandPrefix;
import requests.AcceptRequest;

public class DistributedTable {
	private static DistributedTable distributedTable = null; // singleton instance
	private Hashtable<String, String> transactionStore;
	
	private DistributedTable() {
		this.transactionStore = new Hashtable<String, String>();
	}
	
	public static DistributedTable getInstance() {
		if (distributedTable == null) {
			distributedTable = new DistributedTable();
		}
		return distributedTable;
	}
	
	public synchronized boolean processRequest(AcceptRequest changes) {
		boolean isRequestingNode;
		try {
			isRequestingNode = DistributedNode.getInstance().nodeId == changes.proposalId.nodeId;
		} catch (IOException e) {
			return false;
		}
		for (Command cmd: changes.cmds){
			if (isRequestingNode && cmd.commandType == CommandPrefix.GET) {
				System.out.printf("GET %s: %s", cmd.key, cmd.value);
			} else if (cmd.commandType == CommandPrefix.PUT) {
				updateValue(cmd.key, cmd.value);
			} else if (cmd.commandType == CommandPrefix.DELETE) {
				deleteValue(cmd.key);
			}
		}
		return true;
	}

	private void updateValue(String key, String value) {
		transactionStore.put(key, value);
	}
	
	private void deleteValue(String key) {
		transactionStore.remove(key);
	}
}


