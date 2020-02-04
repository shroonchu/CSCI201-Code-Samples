package sharonxi_CSCI201L_Assignment4;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

public class Configuration {
	static Properties ConfigProps = null;
	static String fileName = null;
	static boolean validfile = false;
	// Tries to find file from filename
	// Returns true if found, false if not found
	public static boolean findConfigFile(String filename) {
		fileName = filename;
		try {
			InputStream input = new FileInputStream(fileName);
			ConfigProps = new Properties();
		    // load a properties file
		    ConfigProps.load(input);
        } catch (IOException ioe) {
            return false;
        }
		return true;
	}
	// Tries to fill in properties into Configuration read from the file
	// If any 
	public static String readConfigFile() {
		// Prepare error message if a parameter is missing or invalid
		String currentProp = null;
		String error = " is a required parameter in the configuration file.";
		// Set vector to store values
		Vector<String> values = new Vector<String>();
		// Set vector of keys we expect
		Vector<String> keys = new Vector<String>();
		keys.add("ServerHostname");
		keys.add("ServerPort");
		keys.add("DBConnection");
		keys.add("DBUsername");
		keys.add("DBPassword");
		keys.add("SecretWordFile");
		try {
			// Iterate through vector and check for key in file
			for(int i=0; i<keys.size(); i++) {
				// Update current property that we are at
				currentProp = (String)keys.get(i);
				// If the file does not contain this property, it throws an exception
				String value = ConfigProps.getProperty(currentProp).trim();
				// Property exists. Check if content is valid
				if(value == null || value.isEmpty()){
					return currentProp + error;
				}
				// If the current property is ServerPort, check if numeric
				if(currentProp.contentEquals("ServerPort")) {
					try {
						Integer.parseInt(value);
					}
					catch(NumberFormatException nfe) {
						return currentProp + " must be numeric.";
					}
				}
				// If the current property is SecretWordFile, check if exists
				if(currentProp.contentEquals("SecretWordFile")) {
					try {
						InputStream checksecretwordfile = new FileInputStream(value);
			        } catch (IOException ioe) {
			            return currentProp + " " + value + " does not exist.";
			        }
				}
				// If the property exists and is valid, add it to 
				// the values vector
				values.add(value);
			}
		}
		catch (NullPointerException npe) {
			return currentProp + error;
		}
		// All values exist and are valid. Change validfile to true.
		validfile = true;
		//Create a string that returns all data
		String properties = keys.get(0) + " - " + values.get(0) + "\n" 
							+ keys.get(1) + " - " + values.get(1) + "\n" 
							+ keys.get(2) + " - " + values.get(2) + "\n"  
							+ keys.get(3) + " - " + values.get(3) + "\n" 
							+ keys.get(4) + " - " + values.get(4) + "\n" 
							+ keys.get(5) + " - " + values.get(5) + "\n" ;
		return properties;
	}
}
