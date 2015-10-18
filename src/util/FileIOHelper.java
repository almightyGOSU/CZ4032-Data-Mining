package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class to help with file I/O operations
 */
public class FileIOHelper {
	
	/**
	 * Appends the given contents to an existing file (if any)<br>
	 * Creates a new file and stores the contents if no such file exists
	 * 
	 * @param content	Contents to be saved to file
	 * @param fileName	Name of file
	 */
	public static void appendToFile(String content, String fileName) {
		
		saveToFile(content, fileName, false);
	}
	
	/**
	 * Stores the given contents in a file
	 * 
	 * @param content	Contents to be saved to file
	 * @param fileName	Name of file
	 * @param bAppend	Indicates whether to append or truncate
	 */
	public static void saveToFile(String content, String fileName, boolean bAppend) {
		
		try {
			FileWriter fw = new FileWriter(new File(fileName), bAppend);
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a BufferedReader to the given input file
	 * 
	 * @param filename	File name
	 * @return	BufferedReader to the given input file
	 */
	public static BufferedReader readDataFile(String filename) {
		
		BufferedReader inputReader = null;
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
		return inputReader;
	}
}