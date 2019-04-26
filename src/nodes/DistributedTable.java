package nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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

	public String getValue(String key) {
		return transactionStore.get(key);
	}
	
	public List<String> getTableAsList() {
		List<String> tableCopy = new ArrayList<>();
		synchronized(this.transactionStore) {
			for (String key: transactionStore.keySet()) {
				tableCopy.add(String.format("K: %s V: %s", key, transactionStore.get(key)));
			}
		}
		
		return tableCopy;
	}
	
	private void updateValue(String key, String value) {
		transactionStore.put(key, value);
	}
	
	private void deleteValue(String key) {
		transactionStore.remove(key);
	}
}


