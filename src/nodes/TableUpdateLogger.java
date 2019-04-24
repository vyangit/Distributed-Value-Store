package nodes;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;

import commands.Command;
import requests.AcceptRequest;
import structs.DateParser;

public class TableUpdateLogger {
	
	/**
	 * The cache size of the logger before it has to dump into main memory
	 */
	public int logCacheSize;
	
	private PriorityQueue<AcceptRequest> log;
	private int lowestIndex;
	private String dumpFolderPath;
	
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
		this.log = new PriorityQueue<AcceptRequest>(logCacheSize, new Comparator<AcceptRequest>() {
			@Override
			public int compare(AcceptRequest a, AcceptRequest b) {
				return a.proposalId.timestamp.compareTo(b.proposalId.timestamp);
			}
		});
		this.logCacheSize = logCacheSize;
		this.lowestIndex = 0;
		File dumpFolder = new File(dumpFolderPath);
		if (dumpFolder.exists() && dumpFolder.isDirectory()) {
			this.dumpFolderPath = dumpFolderPath;
		} else {
			System.out.println("Failed to find dump folder specified, default folder created");
			dumpFolderPath = "./paxos_dump";
			dumpFolder = new File(dumpFolderPath);
			dumpFolder.mkdir();
		}
	}
	
	/**
	 * @param commandHistory
	 * @return True if successfully added, else false
	 */
	public boolean append(AcceptRequest commandHistory) {
		if (lowestIndex == logCacheSize) {
			dumpLog();
		}
		if (!log.offer(commandHistory)) {
			return false;
		}
		
		DistributedTable table = DistributedTable.getInstance();
		table.processRequest(commandHistory);
		lowestIndex++;
		return true;
	}
	
	private synchronized boolean dumpLog() {		
		String dumpFileDate = DateParser.parseLogDate(new Date());
		File dumpFile = new File(String.format("%s%s_paxos_dump.txt", dumpFolderPath, dumpFileDate));
		try {
			Thread.sleep(1);
			dumpFile.createNewFile();
			
			FileOutputStream out = new FileOutputStream(dumpFile);
			while (!log.isEmpty()) {
				AcceptRequest acceptedChange = log.peek();
				// Write the header
				out.write(acceptedChange.proposalId.toString().getBytes()); // write header
				for (Command cmd: acceptedChange.cmds) {
					out.write("\n".getBytes()); // Start new line
					out.write(String.format("%s\n", cmd.toString()).getBytes()); // write command
				}
				
				// Change successfully logged
				out.flush();
				lowestIndex--;
				log.poll(); 
			}
			
			out.close();
			return true;
		} catch (Exception e) {
			return false; // Failures occurred
		}
	}
}
