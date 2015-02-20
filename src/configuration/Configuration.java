package configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Configuration {
	private static HashMap<String, String> config = new HashMap<String, String>();

	private static ArrayList<String> validParameters = new ArrayList<String>();
	private static ArrayList<String> validCommands = new ArrayList<String>();
	
	private static ArrayList<String> neededParameters = new ArrayList<String>();

	public static int validParametersSet = 0;
	public static int validCommandsSet = 0;

	private final static Logger LOGGER = Logger.getLogger(Configuration.class);

	public static void setConfiguration(String key, String value) {
		Configuration.config.put(key, value);
	}

	public static void setCommand(String command) {
		Configuration.config.put(command, "true");
	}

	public static String getConfiguration(String key) {
		return Configuration.config.get(key);
	}

	public static boolean getCommand(String command) {
		String s = (String) Configuration.config.get(command);
		if (s.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	public static void addNewValidParameter(String s, boolean needed) {
		Configuration.validParameters.add(s);
		if(needed) neededParameters.add(s);
	}

	public static void addNewValidCommand(String s) {
		Configuration.validCommands.add(s);
	}

	public static void readFromFile(File path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path.getAbsolutePath()));
		String line = null;
		String[] parts;
		while ((line = reader.readLine()) != null) {
			parts = line.split(" ");
			if (Configuration.parameterExists(parts[0])) {
				Configuration.setConfiguration(parts[0], parts[1]);
				Configuration.validParametersSet++;
			} else if (Configuration.parameterIsCommand(parts[0])) {
				Configuration.setConfiguration(parts[0], "1");
				Configuration.validCommandsSet++;
			}
		}
		reader.close();
	}

	public static void readFromRunArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (Configuration.parameterExists(args[i])) {
				String key = args[i].substring(1);
				String value = args[i + 1];
				Configuration.setConfiguration(key, value);
				Configuration.validParametersSet++;
			} else if (Configuration.parameterIsCommand(args[i])) {
				Configuration.setConfiguration(args[i], "1");
				Configuration.validCommandsSet++;
			}
		}
	}

	public static void debugParameters() {
		LOGGER.info("LOADED PARAMETERS DEBUGGING");
		for (String key : Configuration.config.keySet()) {
			LOGGER.info("[" + key + "] = [" + config.get(key) + "]");
		}
	}

	private static boolean parameterIsCommand(String p) {
		for (String s : Configuration.validCommands) {
			if (("--" + s).equals(p)) {
				return true;
			}
		}
		return false;
	}

	private static boolean parameterExists(String p) {
		for (String s : Configuration.validParameters) {
			if (("-" + s).equals(p))
				return true;
		}
		return false;
	}

	public static void verifyArgs() throws Exception {
		for (String s : Configuration.neededParameters) {
			if(!Configuration.config.containsKey(s)) {
				throw new Exception("Missing needed parameter: " + s);
			}
		}
		
	}
}
