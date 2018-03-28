import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;

import data.BioTree;
import data.DataStore;
import data.Date;
import data.FileProcessor;
import data.Record;
import data.WormsAPI;
import search.BST;
import search.BasicSearch;
import search.BasicSearchResult;
import search.Histogram;
import sort.Bound;
import sort.GeneralRange;
import sort.KDT;
import sort.RangeHelper;
import utils.Stopwatch;

public class Main {
	public static void main(String[] args) {
		printLogo();
		//load data
		try {
			BioTree.init("data/biotree/");
			DataStore.records = new KDT<Record>("data/kdt.ser");
		} catch (Exception e0) {
			try {
				BioTree.init();
				FileProcessor.initProcessing();
			} catch (NumberFormatException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BioTree.write("data/biotree/");
			DataStore.records.writeToFile("data/kdt.ser");
		};
		init();
	}
	
	private static void printLogo() {
		System.out.println("======== TRAWLEXPERT ALPHA v1 ========");
		System.out.println("                                   _...----.\r\n" + 
				"                       _,      _,-'_...--'`/\r\n" + 
				"                     ,^__\\__ ,'  ,'      /(\r\n" + 
				"                 _,.'  |  | `-._;        \\/\r\n" + 
				"              ,-'  |  /  /   \\  \\-.       \\\r\n" + 
				"            ,'   \\ \\  \\ <    /  <  \\     ,:\r\n" + 
				"           /     | <   `.\\   >  /   \\  ,' |    _,-'/\r\n" + 
				"          /      /  \\        \\  \\    \\/  _; ,-'   ;\r\n" + 
				"         /       `.  \\       /   \\   /`<__,' \\   /|\r\n" + 
				"        ; (O)      > |       >   <   \\ \\`^.  /   :|\r\n" + 
				"       /         :\\  |_.--. <    ,`  / /  (  >   :|\r\n" + 
				"       >)        ;/,='   `.\\ \\   |  / / __/  \\   ;|\r\n" + 
				"       \\       ,' |)     ,'/ /   |  ) |/-.`. /   \\|\r\n" + 
				"        `.   ,'   | `--=='`  >   |  `./`-.\\ `-._  :\r\n" + 
				"          `.      / /        \\   \\   ;`.__/     `-.\\\r\n" + 
				"            `-._  \\ |_       :   < _;    <   SSt\r\n" + 
				"                ``'.:``-._____\\_,-'-..___,\\\r\n" + 
				"                     \\`.|`._\\  `--..__     `._\r\n" + 
				"                      `-'             `````````");
		System.out.println("Loading.......");
	}
	
	public static void init() {
		System.out.println("Welcome!");
		while(true) {
			System.out.println("Main Menu");
			System.out.println("Available commands:");
			System.out.println("\ttree [taxonId / scientific name]");
			System.out.println("\trecords (taxonId / scientific name) [-t start end]");
			System.out.print("> ");
			Pattern pat = Pattern.compile("([a-zA-Z]+)[ ]?([0-9a-zA-Z ]+[0-9a-zA-Z])?[ ]?[-]?([a-zA-Z])?[ ]?([A-Za-z0-9]+)?[ ]?([A-Za-z0-9]+)?[ ]?([A-Za-z0-9]+)?[ ]?([A-Za-z0-9]+)?[ ]?([A-Za-z0-9]+)?[ ]?");
			Scanner s = new Scanner(System.in);
			String line = s.nextLine();
			Matcher matcher = pat.matcher(line);
			if (!matcher.find()) continue;
			
			//tree
			//tree taxonId
			//tree scientific name
			//records taxonId
			//records scientific name
			String command = matcher.group(1);
			
			if (command.equals("records"))
				rangeSearch(matcher);
			else if (command.equals("tree"))
				printTree(matcher);
		}
	}
	
	private static void rangeSearch(Matcher matcher) {
		Integer start = null;
		Integer end = null;
		if (matcher.group(3) != null)
			if (matcher.group(3).equals("t")) {
				if (matcher.group(4) != null)
					start = Integer.parseInt(matcher.group(4));
				if (matcher.group(5) != null)
					end = Integer.parseInt(matcher.group(5));
			}
		
		Integer taxonId = null;
		try {
			taxonId = Integer.parseInt(matcher.group(2));
		} catch (NumberFormatException e) {
			if (taxonId == null) {
				try {
					taxonId = WormsAPI.nameToRecordID(matcher.group(2));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		BasicSearchResult result = BasicSearch.range(taxonId, start, end);
		
		System.out.println("Found " + result.n() + " records in " + result.time() + " seconds.");
		
		while(true) {
			System.out.println("Available commands: list, histogram, sum, exit");
			System.out.print("> ");
			
			Scanner s = new Scanner(System.in);
			String command = s.nextLine();
			
			if (command.equals("list"))
				printRecords(result.results());
			else if (command.equals("histogram")) {
				printHistogram(result.histogram());
			} else if (command.equals("exit"))
				return;
			else if (command.equals("sum")) {
				System.out.println(result.sum());
			}
		}
	}
	
	private static void printRecords(Iterable<Record> results) {
		String format = "|%1$-45s|%2$-15s|%3$-15s|%4$-15s|%5$-15s|%6$-15s\n";
		System.out.format(format, "Scientific Name", "IndividualCount", "Latitude", "Longitude","Year","Month","Day");
		for (Record r: results) {
			System.out.println(r);
		}
	}
	
	private static void printTree(Matcher matcher) {
		Integer taxonId;
		String name;
		if (matcher.group(2) == null)
			BioTree.printTree();
		else {
			name = matcher.group(2);
			try {
				taxonId = Integer.parseInt(name);
				BioTree.printTree(taxonId);
			} catch (Exception e) {
				BioTree.printTree(name);
			}
		}
		System.out.println();
	}
	
	/**
	 * Prints a histogram based on a BST of records
	 * 
	 * @param record -An BST of records
	 */
	public static void printHistogram(BST<Integer,Integer> record) {
		int max = 0;
		int scale = 100;
		Iterable<Integer> results = record.keys();
		for (Integer year: results) {
			if (max < record.get(year)) max =record.get(year);

		}
		System.out.println("     |" + (new String(new char[scale]).replace('\0', '-')) + "|");
		String format = "%1$-5d|%2$-" + (scale + 1) + "s";
		for (Integer year: results) {
			String s = "=";
			int loopc = (int) ((float)(record.get(year)/ (float) max) * scale);
			for (int j=0; j< loopc; j++) {
				s+="=";
			}
			System.out.format(format, year, s);
			System.out.println("| " + record.get(year));
		}
		System.out.format("Scale: one = is %d individuals.\n", max / scale);
	}
}