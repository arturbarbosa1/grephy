/**
   * file: Grep.java
   * author: Artur Barbosa
   * course: CMPT 440
   * assignment: Grephy 
   * due date: 5/7/2018
   * Version: 1
   * 
   * This file contains the Grep.java file for my grep project.
   *
   */
package grephy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Grep {

	private static List<String> getLinesFromFile(String filename) throws Exception {
		Scanner fileScanner = new Scanner(new File(filename));
		String line;
		List<String> lines = new ArrayList<String>();
		
		while(fileScanner.hasNextLine()) {
			line = fileScanner.nextLine();
			lines.add(line);
		}
		fileScanner.close();
		
		return lines;
	}
	
	private static String getAlphabetsFromLines(List<String> lines) {
		String alphabet = "";
		for(String line : lines) {
			for(int i=0; i < line.length(); i++) {
				char c = line.charAt(i);
				if(!alphabet.contains(c+""))
					alphabet += c;
			}
			
		}
		return alphabet;
	}
	
	
	public static void main(String[] args) throws Exception {
		
		if(args.length < 2) {
			System.out.println("\nUsage: java [-n NFA-FILE] [-d DFA-FILE] grephy.Grep REGEX FILE");
			System.exit(1);
		}
		String nfaFilename = null;
		String dfaFilename = null;
		String regex=null;
		String filename=null;
		
		if(args.length == 4) {
			if(args[0].equals("-n"))
				nfaFilename = args[1];
			else if(args[0].equals("-d"))
				dfaFilename = args[1];
			else {
				System.out.println("\nUsage: java [-n NFA-FILE] [-d DFA-FILE] grephy.Grep REGEX FILE");
				System.exit(1);
			}
			regex = args[2];
			filename = args[3];
		}
		else if(args.length == 6) {
			if(args[0].equals("-n"))
				nfaFilename = args[1];
			else if(args[0].equals("-d"))
				dfaFilename = args[1];
			else {
				System.out.println("\nUsage: java [-n NFA-FILE] [-d DFA-FILE] grephy.Grep REGEX FILE");
				System.exit(1);
			}
			if(args[2].equals("-n"))
				nfaFilename = args[3];
			else if(args[2].equals("-d"))
				dfaFilename = args[3];
			else {
				System.out.println("\nUsage: java [-n NFA-FILE] [-d DFA-FILE] grephy.Grep REGEX FILE");
				System.exit(1);
			}
			regex = args[4];
			filename = args[5];
		}
		else if(args.length == 2) {
			regex = args[0];
			filename = args[1];
		}
		else {
			System.out.println("\nUsage: java [-n NFA-FILE] [-d DFA-FILE] grephy.Grep REGEX FILE");
			System.exit(1);
		}
		
		List<String> lines = getLinesFromFile(filename);
		String alphabet = getAlphabetsFromLines(lines);
		
		NFA nfa = NFA.buildFromRegex(regex, alphabet);
		//System.out.println(nfa.toString()+"\n\n");
		
		for(String line : lines) {
			boolean matched = nfa.validateInput(line);
			if(matched)
				System.out.println(line);
		}
	}

}

