package nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableUpdateLogger {
	List<List<String>> log;
	int logCacheSize;
	int lowestIndex;
	String dumpFolderPath;
	
	/**
	 * Constructs a logger with a cache size of 100
	 */
	public TableUpdateLogger() {
		this(100, "./");
	}
	
	/**
	 * Constructs a logger with the indicated cache size
	 * 
	 * @param logCacheSize The maximum amount of logs to be kept before dumping into the drive
	 */
	public TableUpdateLogger(int logCacheSize) {
		this(logCacheSize, "./");
	}
	
	/**
	 * Constructs a logger with the indicated cache size and dump folder when cache is reached
	 * 
	 * @param logCacheSize The maximum amount of logs to be kept before dumping into the drive
	 * @param dumpFolderPath The path to the folder to dump the logs
	 */
	public TableUpdateLogger(int logCacheSize, String dumpFolderPath) {
		if (logCacheSize < 0) {
			this.log = Collections.synchronizedList(new ArrayList<List<String>>());
		} else {
			this.log = Collections.synchronizedList(new ArrayList<List<String>>(logCacheSize));	
		}
		this.logCacheSize = logCacheSize;
		this.lowestIndex = 0;
		this.dumpFolderPath = dumpFolderPath;
	}
	
	/**
	 * @param commandHistory
	 * @return
	 */
	public boolean append(List<String> commandHistory) {
		log.add(commandHistory);
		lowestIndex++;
		if (lowestIndex == logCacheSize) {
			dumpLog();
		}
		return true;
	}
	
	private synchronized boolean dumpLog() {
		lowestIndex=0;
		return true;
	}
}
