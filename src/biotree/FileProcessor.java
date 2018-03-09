package biotree;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner; // Testing ONLY
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class reads and parses files in the format of occurences.csv
 * It provides methods to get chunks of data. 
 * 
 * Notes:
 * - Processes one row at a time
 * - Parsed data is in string format
 * - TOO MANY FUNCTION CALLS?
 */
public class FileProcessor {
	private static String path = "src/occurrence.csv";
	
	/**
	 * Gets the path string
	 * 
	 * @return Path String
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Sets the path string
	 */
	public void setPath(String newPath) {
		path = newPath;
	}
	
	/**
	 * Initialize Processing.
	 *  Reads file at path. 
	 *  Calls parse() for each line
	 */
	private static void initProcessing() {
		FileReader fr;
		BufferedReader br;

		try {
			fr = new FileReader(path);
			br = new BufferedReader(fr);
			
			Scanner s = new Scanner(System.in); //Testing ONLY
			String currentLine;
			
			br.readLine();	// Reads Past Field Names
			int i = 0;
			while ((currentLine = br.readLine()) != null) {
				//System.out.println(currentLine); //Testing ONLY for checking one line at a time
				i++;
				System.out.println("Processed line " + i);
				parse(currentLine);
				//s.nextLine(); //Testing ONLY for checking one line at a time
			}
			
			s.close();//Testing ONLY
			br.close();
			fr.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses data from string
	 *  Calls createObject on successful regex matching
	 * 
	 * @param currentLine, a line/row of data
	 * @throws IOException
	 */
	private static void parse(String currentLine) throws IOException {
		/* Regex Pattern Grouping Guide
		 * 	Retrieve String Groups with: matchEventId.group(x):
		 *  group 0: full matched string
		 *  group 1: occurrenceId
		 *  group 2: individualCount
		 *  group 3: eventId
		 *  group 4: year
		 *  group 5: month
		 *  group 6: date
		 *  group 7: latitude
		 *  group 8: longitude
		 *  group 9: taxonId
		 *  group 10: Scientific Name
		 */
		Pattern patternEventId = Pattern.compile("([^,]+)?,([^,]+)?,[^,]+,[^,]+,[^,]+OP_ID (\\d+)?,(\\d+)?-(\\d+)?-(\\d+)?,(\\d+.\\d+)?,(-\\d+.\\d+)?,[^,\\d]+(\\d+)?,([^,]+)?");
		Matcher matchEventId = patternEventId.matcher(currentLine);

		if(matchEventId.find()) {
			// Testing ONLY Print lines
//			System.out.println("Full String:" + matchEventId.group(0));
//			System.out.println("Occurence Id:" + matchEventId.group(1));
//			System.out.println("Ind. Count:" + matchEventId.group(2));
//			System.out.println("event Id:" + matchEventId.group(3));
//			System.out.println("Year:" + matchEventId.group(4));
//			System.out.println("Month:" + matchEventId.group(5));
//			System.out.println("Day:" + matchEventId.group(6));
//			System.out.println("lat:" + matchEventId.group(7));
//			System.out.println("long:" + matchEventId.group(8));
//			System.out.println("tax Id:" + matchEventId.group(9));
//			System.out.println("Scientific Name:" + matchEventId.group(10));
			
			System.out.println("OccurID:" + matchEventId.group(1));	// FOR TESTING ONLY
			createObjects(matchEventId);
		}
		else {
			System.out.println("Regex Matching Failed.");
		}		
	}
	
	/**
	 * Calls BioTree's processRecord and another method to create a Record
	 * 
	 * @param matchEventId
	 */
	public static void createObjects(Matcher matchEventId) {
		// Call BioTree
		Record rec = null;
		if(matchEventId.group(9) != null) {
			Integer res = BioTree.processRecord(Integer.parseInt(matchEventId.group(9)));
			// Create Record Object
			if (res != null)
				rec = createRecord(Integer.parseInt(matchEventId.group(3)), matchEventId.group(1), Integer.parseInt(matchEventId.group(9)), Integer.parseInt(matchEventId.group(2)), Float.parseFloat(matchEventId.group(7)), Float.parseFloat(matchEventId.group(8)), Integer.parseInt(matchEventId.group(4)), Integer.parseInt(matchEventId.group(5)), Integer.parseInt(matchEventId.group(6)));
		}
		else if (! matchEventId.group(10).equals("NA")) {
			try{
				Integer taxonId = BioTree.processRecord(matchEventId.group(10));
				if (taxonId != null)
					rec = createRecord(Integer.parseInt(matchEventId.group(3)), matchEventId.group(1), taxonId, Integer.parseInt(matchEventId.group(2)), Float.parseFloat(matchEventId.group(7)), Float.parseFloat(matchEventId.group(8)), Integer.parseInt(matchEventId.group(4)), Integer.parseInt(matchEventId.group(5)), Integer.parseInt(matchEventId.group(6)));
			} catch(IOException e) {
				System.out.println("No Taxon ID or Scientific Name. OR createRecord Error");
			}
		}
		//do something with rec
	}
	
	/**
	 * Create a Record Object
	 * 
	 * @param eventId
	 * @param occurId
	 * @param taxonId
	 * @param individualCount
	 * @param latitude
	 * @param longitude
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Record createRecord(int eventId, String occurId, int taxonId, int individualCount, float latitude, float longitude, int year, int month, int day) {
		return new Record(eventId, occurId, taxonId, individualCount, latitude, longitude, year, month, day);
	}
	
	public static void main(String[] args) {
		initProcessing();
	}
}
