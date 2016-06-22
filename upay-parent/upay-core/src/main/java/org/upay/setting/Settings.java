package org.upay.setting;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.upay.exception.UException;
import org.upay.exception.URuntimeException;
import org.upay.logger.Logger;
import org.upay.logger.LoggerFactory;
import org.upay.util.ResourceUtil;

public class Settings {
	
	//-----------------------------Singleton instance
	private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
	
	private static final String DEFAULT_CONFIG_PATH = "upay.xml";
	
	private static volatile Settings instance;
	
	//-----------------------------configuration
	/**
	 * properties
	 */
	private volatile Properties variables;
	
	//--------settings
	private volatile Map<String, Object> settings;
	
	//--------managers
	static{
		try {
			LOGGER.info("loading default configuration {0}.", DEFAULT_CONFIG_PATH);
			load();
			LOGGER.info("default configuration {0} loaded.", DEFAULT_CONFIG_PATH);
		} catch (UException e) {
			LOGGER.warn("could not load default configuration {0} from classpath, rexdb is not initialized, cause {1}", DEFAULT_CONFIG_PATH, e.getMessage());
		}
	}
	
	/**
	 * Loads the default configuration.
	 * 
	 * @throws UException if the default XML file does not exist, could not parse the file, etc.
	 */
	public synchronized static void load() throws UException{
		if(instance != null)
			throw new UException("configuration has been loaded, could not load" + DEFAULT_CONFIG_PATH);
		
		InputStream inputStream = ResourceUtil.getResourceAsStream(DEFAULT_CONFIG_PATH);
		if(inputStream == null){
			LOGGER.warn("could not find configuration {0} in classpath.", DEFAULT_CONFIG_PATH);
		}else
			instance = new XMLLoader().load(inputStream);
	}
	
	/**
	 * Loads the default configuration from classPath.
	 * @param path file in classPath.
	 * @throws UException if the default XML file does not exist, could not parse the file, etc.
	 */
	public synchronized static void loadFromClasspath(String path) throws UException{
		if(instance != null)
			throw new UException("configuration has been loaded, could not load" + path);
		
		LOGGER.info("loading configuration {0} from classpath.", path);
		instance = new XMLLoader().loadFromClasspath(path);
		LOGGER.info("configuration {0} loaded.", path);
	}
	
	/**
	 * Loads the default configuration from classPath.
	 * @param path file absolute path.
	 * @throws UException if the default XML file does not exist, could not parse the file, etc.
	 */
	public synchronized static void loadFromFileSystem(String path) throws UException{
		if(instance != null)
			throw new UException("configuration has been loaded, could not load" + path);
		
		LOGGER.info("loading configuration {0} from file system.", path);
		instance = new XMLLoader().loadFromFileSystem(path);
		LOGGER.info("configuration {0} loaded.", path);
	}

	/**
	 * Returns current configuration.
	 */
	public static Settings getCurrentSettings() throws URuntimeException{
		if(instance == null){
			try {
				load();
			} catch (UException e) {
				throw new URuntimeException(e);
			}
		}
		
		if(instance == null)
			throw new URuntimeException("configuration not loaded, please check if the default configuration "+DEFAULT_CONFIG_PATH+" is correct.");
			
		return instance;
	}
	
	
	//---------------------------
	public Settings(){
		settings = new HashMap<String, Object>();
	}
	
	public void addVariables(Properties variables) {
		if(this.variables == null) 
			this.variables = variables;
		else
			this.variables.putAll(variables);
	}

	public Properties getVariables() {
		return variables;
	}
	
	public void addSetting(String id, Object setting){
		settings.put(id, setting);
	}

	public Object getSetting(String id){
		return settings.get(id);
	}
	
	public boolean containsSetting(String id){
		return settings.containsKey(id);
	}
}
