package main;

import java.io.IOException;
import java.util.Scanner;

import nodes.DistributedNode;
import utils.CommandManager;
import utils.StringParser;

public class PaxosMain {
	
	/**
	 * @param args[0] Entry node remote ip address
	 */
	
	public static void main(String[] args) {
		// Acquiring local node addr

		System.out.println("-- Starting up seeding node --");
		DistributedNode paxosNode = null;
		try {
			paxosNode = new DistributedNode();
			System.out.println("-- Node created --");
			System.out.printf("Server address: %s\n", paxosNode.serverIp);
			System.out.printf("Server port (Use this to connect to server): %d\n", paxosNode.getServerSocket().getLocalPort());
			System.out.printf("Paxos port: %d\n", paxosNode.getPaxosSocket().getLocalPort());

		} catch (IOException e) {
			System.out.println("-- Seeding failed: Shutting down paxos node --");
			System.exit(1);
		}
		
		if (args.length == 0) {
			// Initialed seeder node does nothing
		} else if (args.length == 1){
			// Initialize connecting node
			String remoteAddress = args[0];
			
//			System.out.println("-- Establishing connection to "+ remoteAddress +" --");
//			if (ConnectionManager.establishConnection(paxosNode,  remoteAddress)) {
//				System.out.println("-- Connection with "+remoteAddress+" established --");
//			} else {
//				System.out.println("-- Connection with "+remoteAddress+" failed --");
//				System.out.println("-- Shutting down paxos node --");
//				System.exit(0);
//			}			
		}
		
		// Common paxos node logic
		Scanner cmdStream = new Scanner(System.in);
		boolean quitFlag = false;
		
		while (!quitFlag) {
			System.out.println("Input a command, 'help' for valid commands, or 'exit' to shutdown the node: ");
			
			String[] cmd = StringParser.splitWords(cmdStream.nextLine().trim());
			if (!CommandManager.isValidCommand(cmd)) {
				System.out.println("Invalid command");
				continue;
			}
			
			if (cmd[0] == "put") {
				
			} else if (cmd[0] == "get") {
				
			} else if (cmd[0] == "help") {
				
			} else if (cmd[0] == "exit") {
				break;
			}
		}
		
		System.out.println("-- Shutting down paxos node --");
		cmdStream.close();
		System.exit(0);
	}
}
