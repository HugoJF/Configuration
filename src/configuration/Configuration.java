package configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Configuration {
	/**
	 * HashMap containing set values for parameters and commands
	 */
	private static HashMap<String, String> config = new HashMap<String, String>();

	/**
	 * Array containing valid parameter and command names
	 */
	private static ArrayList<String> validParameters = new ArrayList<String>();
	private static ArrayList<String> validCommands = new ArrayList<String>();

	/**
	 * Array containing needed parameters for execution
	 */
	private static ArrayList<String> neededParameters = new ArrayList<String>();

	/**
	 * Counters for valid parameters and commands
	 */
	public static int validParametersSet = 0;
	public static int validCommandsSet = 0;

	/**
	 * The log4j class
	 */
	private final static Logger LOGGER = Logger.getLogger(Configuration.class);

	/**
	 * Added parameter and it's value to the configuration HashMap
	 * 
	 * @param key - the parameter name
	 * @param value - the parameter value
	 */
	public static void setConfiguration(String key, String value) {
		Configuration.config.put(key, value);
	}

	/**
	 * Set command as executed
	 * 
	 * @param command - the command to be executed
	 */
	public static void setCommand(String command) {
		Configuration.config.put(command, "true");
	}

	/**
	 * Return value for set command or parameter
	 * 
	 * @param key - command or parameter name
	 * @return the command/parameter set value
	 */
	public static String getConfiguration(String key) {
		return Configuration.config.get(key);
	}

	/**
	 * Check if command is set
	 * 
	 * @param command - the command name
	 * @return true if command if set, false if it's not
	 */
	public static boolean isCommandSet(String command) {
		String s = (String) Configuration.config.get(command);
		if (s == null) return false;
		if (s.equalsIgnoreCase("true")) return true;
		return false;
	}

	/**
	 * Adds a new valid parameter to be set
	 * 
	 * @param s - the parameter name to be added
	 * @param needed - if it's required for execution, verifyArgs() throw error
	 *        if a needed parameter is not set
	 */
	public static void addNewValidParameter(String s, boolean needed) {
		Configuration.validParameters.add(s);
		if (needed) neededParameters.add(s);
	}

	/**
	 * Adds a new valid parameter to be set defaulted to not be needed
	 * 
	 * @param s - the parameter name to be added
	 */
	public static void addNewValidParameter(String s) {
		Configuration.validParameters.add(s);
	}

	/**
	 * Adds a new valid command to be set
	 * 
	 * @param s - the command name to be added
	 */
	public static void addNewValidCommand(String s) {
		Configuration.validCommands.add(s);
	}

	/**
	 * Reads the entire configuration set from a file
	 * 
	 * @param path - a File object containing config file
	 * @throws IOException any error caused by reading the file
	 */
	public static void readFromFile(File path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path.getAbsolutePath()));
		String line = null;
		String[] parts;
		while ((line = reader.readLine()) != null) {
			parts = line.split(" ");
			if (Configuration.parameterExists(parts[0])) {
				Configuration.setConfiguration(parts[0], parts[1]);
				Configuration.validParametersSet++;
			} else if (Configuration.isCommand(parts[0])) {
				Configuration.setConfiguration(parts[0], "true");
				Configuration.validCommandsSet++;
			}
		}
		reader.close();
	}

	/**
	 * @param args - String array from main method
	 */
	public static void readFromRunArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (Configuration.parameterExists(args[i])) {
				String key = args[i].substring(1);
				String value = args[i + 1];
				Configuration.setConfiguration(key, value);
				Configuration.validParametersSet++;
			} else if (Configuration.isCommand(args[i])) {
				Configuration.setConfiguration(args[i].substring(2), "true");
				Configuration.validCommandsSet++;
			}
		}
	}

	/**
	 * Print entire configuration key set, containing all set command and/or
	 * parameters
	 */
	public static void debugParameters() {
		LOGGER.info("LOADED PARAMETERS DEBUGGING");
		for (String key : Configuration.config.keySet()) {
			LOGGER.info("[" + key + "] = [" + config.get(key) + "]");
		}
	}

	/**
	 * Check if String is structured as a command in command line
	 * 
	 * @param p - the String to check
	 * @return true if it's structured as command, false if not
	 */
	private static boolean isCommand(String p) {
		for (String s : Configuration.validCommands) {
			if (("--" + s).equals(p)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if a parameter is already listed as a valid parameter
	 * 
	 * @param p - the parameter name to check existance
	 * @return true if parameter exists, false if not
	 */
	private static boolean parameterExists(String p) {
		for (String s : Configuration.validParameters) {
			if (("-" + s).equals(p)) return true;
		}
		return false;
	}

	/**
	 * Verify if all needed parameters are set
	 * 
	 * @throws Exception a missing needed parameter
	 */
	public static void verifyArgs() throws Exception {
		for (String s : Configuration.neededParameters) {
			if (!Configuration.config.containsKey(s)) {
				throw new Exception("Missing needed parameter: " + s);
			}
		}

	}

	/**
	 * Return raw configuration HashMap
	 * 
	 * @return config HashMap<String, String>
	 */
	public static HashMap<String, String> getConfig() {
		return config;
	}
}
