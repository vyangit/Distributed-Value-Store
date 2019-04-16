package nodes;

import java.time.Instant;
import java.util.Hashtable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import enums.ReqType;
import structs.Proposal;

public class DistributedTable {
	DistributedTable distributedTable;
	Hashtable<String, ManagedValue> transactionStore;
	
	private class ManagedValue {
		Lock lock;
		String value;
		Instant timestamp;
		
		public ManagedValue(String value, Instant timestamp) {
			this.lock = new ReentrantLock();
			this.value = value;
			this.timestamp = timestamp;
		}
	}
	
	private DistributedTable() {
		this.transactionStore = new Hashtable<String, ManagedValue>();
	}
	
	public DistributedTable getInstance() {
		if (this.distributedTable == null) {
			distributedTable = new DistributedTable();
		}
		return this.distributedTable;
	}
	
	public boolean processRequest(Proposal proposal) {
		return false;
	}

	private boolean updateValue(String key, String value) {
		return false;
	}
	
	private boolean deleteValue(String key) {
		return false;
	}
}


