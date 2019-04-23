package nodes;

import java.time.Instant;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import requests.PrepareRequest;

public class DistributedTable {
	private DistributedTable distributedTable; // singleton instance
	private Hashtable<String, String> transactionStore;
	
//	private class ManagedValue {
//		Lock lock;
//		String value;
//		Instant timestamp;
//		
//		public ManagedValue(String value, Instant timestamp) {
//			this.lock = new ReentrantLock();
//			this.value = value;
//			this.timestamp = timestamp;
//		}
//	}
	
	private DistributedTable() {
		this.transactionStore = new Hashtable<String, String>();
	}
	
	public DistributedTable getInstance() {
		if (this.distributedTable == null) {
			distributedTable = new DistributedTable();
		}
		return this.distributedTable;
	}
	
	public boolean processRequest(PrepareRequest proposal) {
		return false;
	}

	private void updateValue(String key, String value) {
		transactionStore.put(key, value);
	}
	
	private void deleteValue(String key) {
		transactionStore.remove(key);
	}
}


