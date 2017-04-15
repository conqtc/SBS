/**
 * Utility class which provide common methods for the system
 * 
 * @author Cuong Truong (101265224)
 * @version 0.1
 */

import java.util.*;
import java.text.*;
import java.io.*;

public class Utility {
	// 
	public static final String FILE_MEMBERS = "Members.txt";
	public static final String FILE_SPORTS = "Sports.txt";
	public static final String FILE_BOOKINGS = "Bookings.txt";
	private static final String FILE_LOG="Logs.txt";
	
	
	// scanner object to read user input from keyboard
	private Scanner scanner;
	
	// error writer
	private PrintWriter writer;

    /**
     * Default constructor
     */
    public Utility() {
    	// read from user keyboard
    	scanner = new Scanner(System.in);
    	
    	// writer to LOG file
    	try {
    		writer = new PrintWriter(new FileOutputStream(FILE_LOG));
    		loglnMsg("(i) Writing to log file [" + FILE_LOG +"]");
    		loglnMsg("(i) Runtime [" + DateAndTime.now().toString("hh:mm a MMMM dd, yyyy") + "]");
    	} catch (FileNotFoundException fne) {
    		// ignore file not found, create blank file if not exist
    	}
    }
    
    /**
     * "destructor" for this class, we call this to clean up objects
     */
    public void destructor() {
    	scanner.close();
    	writer.close();
    }
    
    /**
     * Read data from file to an ArrayList of String
     * @param fileName File name
     * @return ArrayList of String
     * @throws FileNotFoundException If file not found
     */
    public static ArrayList<String> readDataFromFile(String fileName) throws FileNotFoundException {
		Scanner fileScanner = new Scanner(new FileInputStream(fileName));
		ArrayList<String> lines = new ArrayList<>();
		
		while (fileScanner.hasNext()) {
			String line = fileScanner.nextLine().trim();
			//if ( (!stringIsNullOrEmpty(line)) && (!stringIsComment(line)) ) {
			lines.add(line);
			//}
		}
		
    	if (fileScanner != null)
    		fileScanner.close();
    	
    	return lines;
    }
    
    /**
     * Write ArrayList of String to file
     * @param fileName File name
     * @param data ArrayList of String to be written to file
     * @throws FileNotFoundException If file not found, a new file will be created
     */
    public static void writeDataToFile(String fileName, ArrayList<String> data) throws IOException {
    	PrintWriter writer = new PrintWriter(new FileWriter(fileName));
    	
    	for (String line: data) {
    		writer.println(line);
    	}
    	
    	if (writer != null) {
    		writer.close();
    	}
    }
	
    /**
     * simply "clear" console screen by print many lines
     */
    public static void clearConsoleScreen() {
        //for (int i = 0; i < 50; i++)
    	//	System.out.println();
    }
    

    /**
     * Check if a String is null or empty
     * @param text String to be checked
     * @return true if String is null or empty, false otherwise
     */
    public static boolean stringIsNullOrEmpty(String text) {
        if (text == null)
            return true;
            
        if (text.isEmpty())
            return true;
            
        return false;
    }
    
    /**
     * Check if string is comment
     * @param text String to check
     * @return True if comment, false otherwise
     */
    public static boolean stringIsComment(String text) {
    	if (stringIsNullOrEmpty(text)) {
    		return false;
    	}
    	
    	return (text.indexOf("//") == 0);
    }
    
    /**
     * Convert a double number to a currency-style string
     * @param number double number
     * @return String represents the value of number in currency-style
     */
    public static String toCurrencyString(double number) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(number);	
    }
    
    /**
     * Read next integer number from keyboard
     * @return int number, -1 if unable to read
     */
    public int nextInt() {
    	int number = -1;
    	
    	try {
    		// try to parse input line from scanner
    		number = Integer.parseInt(scanner.nextLine());
    	} catch (Exception e) {
    		// if anything wrong happens, just give it -1
    		number = -1;
    	}
    	
    	return number;
    }
    
    /**
     * Print a message and read an int value
     * @param message Message
     * @return Int
     */
    public int logNextInt(String message) {
    	System.out.print(message);
    	
    	int number = -1;
    	String line = scanner.nextLine();
    	try {
    		// try to parse input line from scanner
    		number = Integer.parseInt(line);
    		writer.println(message + number);
    	} catch (Exception e) {
    		// if anything wrong happens, just give it -1
    		number = -1;
    		writer.println(message + " Unable to parse int value " + e.getMessage());
    	}
    	
    	return number;
    }
    
    /**
     * Log for next string
     * @param message Message
     * @return String
     */
    public String logNextLine(String message) {
    	System.out.print(message);
    	
    	String line = scanner.nextLine().trim();
    	writer.println(message + line);
    	
    	return line;
    }
    
    /**
     * Read the whole line from user input
     * @return String line text received from user input
     */
    public String nextLine() {
    	String line ="";
    	
    	try {
    		line = scanner.nextLine();
    	} catch (Exception e) {
    		// if anything wrong happens, just give it an empty line
    		line = "";
    	}
    	
    	return line;
    }

    /**
     * Log message 
     * @param message Message
     */
    public void logMsg(String message) {
    	System.out.print(message);
    	writer.print(message);
    }
    
    /**
     * Log error
     * @param error Error
     */
    public void logErr(String error) {
    	writer.print(error);
    }
    
    /**
     * Log line message
     * @param message Message
     */
    public void loglnMsg(String message) {
    	System.out.println(message);
    	writer.println(message);
    }
    
    /**
     * Log line error
     * @param error Error
     */
    public void loglnErr(String error) {
    	writer.println(error);
    }
    
    /**
     * Wait for enter
     * @param message Message
     */
    public void waitForEnter(String message) {
    	writer.println(message);
    	System.out.print(message);
    	this.nextLine();
    }
}
