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
	private HashMap<String, String> config = new HashMap<String, String>();

	/**
	 * Array containing valid parameter and command names
	 */
	private ArrayList<String> validParameters = new ArrayList<String>();
	private ArrayList<String> validCommands = new ArrayList<String>();

	/**
	 * Array containing needed parameters for execution
	 */
	private ArrayList<String> neededParameters = new ArrayList<String>();

	/**
	 * Counters for valid parameters and commands
	 */
	public int validParametersSet = 0;
	public int validCommandsSet = 0;

	/**
	 * The log4j class
	 */
	private final Logger LOGGER = Logger.getLogger(Configuration.class);

	/**
	 * Added parameter and it's value to the configuration HashMap
	 * 
	 * @param key - the parameter name
	 * @param value - the parameter value
	 */
	public void setConfiguration(String key, String value) {
		this.config.put(key, value);
	}

	/**
	 * Set command as executed
	 * 
	 * @param command - the command to be executed
	 */
	public void setCommand(String command) {
		this.config.put(command, "true");
	}

	/**
	 * Return value for set command or parameter
	 * 
	 * @param key - command or parameter name
	 * @return the command/parameter set value
	 */
	public String getConfiguration(String key) {
		return this.config.get(key);
	}

	/**
	 * Check if command is set
	 * 
	 * @param command - the command name
	 * @return true if command if set, false if it's not
	 */
	public boolean isCommandSet(String command) {
		String s = (String) this.config.get(command);
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
	public void addNewValidParameter(String s, boolean needed) {
		this.validParameters.add(s);
		if (needed) neededParameters.add(s);
	}

	/**
	 * Adds a new valid parameter to be set defaulted to not be needed
	 * 
	 * @param s - the parameter name to be added
	 */
	public void addNewValidParameter(String s) {
		this.validParameters.add(s);
	}

	/**
	 * Adds a new valid command to be set
	 * 
	 * @param s - the command name to be added
	 */
	public void addNewValidCommand(String s) {
		this.validCommands.add(s);
	}

	/**
	 * Reads the entire configuration set from a file
	 * 
	 * @param path - a File object containing config file
	 * @throws IOException any error caused by reading the file
	 */
	public void readFromFile(File path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path.getAbsolutePath()));
		String line = null;
		String[] parts;
		while ((line = reader.readLine()) != null) {
			parts = line.split(" ");
			if (this.parameterExists(parts[0])) {
				this.setConfiguration(parts[0], parts[1]);
				this.validParametersSet++;
			} else if (this.isCommand(parts[0])) {
				this.setConfiguration(parts[0], "true");
				this.validCommandsSet++;
			}
		}
		reader.close();
	}

	/**
	 * @param args - String array from main method
	 */
	public void readFromRunArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (this.parameterExists(args[i])) {
				String key = args[i].substring(1);
				String value = args[i + 1];
				this.setConfiguration(key, value);
				this.validParametersSet++;
			} else if (this.isCommand(args[i])) {
				this.setConfiguration(args[i].substring(2), "true");
				this.validCommandsSet++;
			}
		}
	}

	/**
	 * Print entire configuration key set, containing all set command and/or
	 * parameters
	 */
	public void debugParameters() {
		LOGGER.info("LOADED PARAMETERS DEBUGGING");
		for (String key : this.config.keySet()) {
			LOGGER.info("[" + key + "] = [" + config.get(key) + "]");
		}
	}

	/**
	 * Check if String is structured as a command in command line
	 * 
	 * @param p - the String to check
	 * @return true if it's structured as command, false if not
	 */
	private boolean isCommand(String p) {
		for (String s : this.validCommands) {
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
	private boolean parameterExists(String p) {
		for (String s : this.validParameters) {
			if (("-" + s).equals(p)) return true;
		}
		return false;
	}

	/**
	 * Verify if all needed parameters are set
	 * 
	 * @throws Exception a missing needed parameter
	 */
	public void verifyArgs() throws Exception {
		for (String s : this.neededParameters) {
			if (!this.config.containsKey(s)) {
				throw new Exception("Missing needed parameter: " + s);
			}
		}

	}

	/**
	 * Return raw configuration HashMap
	 * 
	 * @return config HashMap<String, String>
	 */
	public HashMap<String, String> getConfig() {
		return config;
	}
}
