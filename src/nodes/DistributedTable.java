package nodes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import commands.AbstractCommand;
import commands.AbstractDistributedCommand;
import commands.DeleteCommand;
import commands.GetCommand;
import commands.ProcessFileCommand;
import commands.PutCommand;
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
		AbstractDistributedCommand cmd = changes.command;
		
		switch(cmd.commandType) {
		case DELETE:
			deleteValue(((DeleteCommand) cmd).key);
			break;
		case PROCESS:
			processCommands(((ProcessFileCommand)cmd).commands);
			break;
		case PUT:
			updateValue(((PutCommand)cmd).key, ((PutCommand)cmd).value);
			break;
		default:
			return false;		
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
	
	private synchronized void processCommands(AbstractCommand[] commands) {
		for (AbstractCommand cmd: commands) {
			switch(cmd.commandType) {
			case DELETE:
				deleteValue(((DeleteCommand) cmd).key);
				break;
			case GET:
				System.out.println(getValue(((GetCommand)cmd).key));
				break;
			case PUT:
				updateValue(((PutCommand)cmd).key, ((PutCommand)cmd).value);
				break;				
			default:
				break;
			}
		}
	}
	
	private void updateValue(String key, String value) {
		transactionStore.put(key, value);
		System.out.println("Put successful");
	}
	
	private void deleteValue(String key) {
		transactionStore.remove(key);
		System.out.println("Delete successful");
	}
}


