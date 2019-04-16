package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StringParser {
	/**
	 * Splits provided string by spaces
	 * 
	 * @param line Line to split
	 * @return Array of split words
	 */
	public static String[] splitWords(String line) {
		List<String> words = new ArrayList<String>();
		
		Scanner lineStream = new Scanner(line);
		
		// Read words from stream
		while (lineStream.hasNext()) {
			words.add(lineStream.next());
		}
		
		// Close stream
		lineStream.close();
		
		return words.toArray(new String[words.size()]);
	}
	
}
