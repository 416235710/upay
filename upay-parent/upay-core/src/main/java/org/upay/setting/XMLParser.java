package org.upay.setting;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.upay.exception.UException;
import org.upay.logger.Logger;
import org.upay.logger.LoggerFactory;
import org.upay.setting.xml.XEntityResolver;
import org.upay.setting.xml.XNode;
import org.upay.setting.xml.XPathParser;
import org.upay.util.ReflectUtil;
import org.upay.util.ResourceUtil;
import org.upay.util.StringUtil;

public class XMLParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLParser.class);

	private XPathParser parser;
	protected final Settings configuration;

	// ----------------constructs
	public XMLParser(InputStream inputStream) {
		this(inputStream, null);
	}

	public XMLParser(InputStream inputStream, Properties props) {
		this(new XPathParser(inputStream, props, new XEntityResolver()), props);
	}

	private XMLParser(XPathParser parser, Properties props) {
		this.configuration = new Settings();
		if(props != null)
			this.configuration.addVariables(props);
		this.parser = parser;
	}

	/**
	 * Parses from root.
	 */
	public Settings parse() throws UException {
		parseSettings(parser.evalNode("/configuration"));
		return configuration;
	}

	private void parseSettings(XNode root) throws UException {
			parsePropertiesNodes(parser.evalNodes(root, "properties"));
			parseSettingsNodes(parser.evalNodes(root, "settings"));
	}
	
	/**
	 * Parses properties nodes.
	 */
	private void parsePropertiesNodes(List<XNode> nodes) throws UException {
		if(nodes == null) return;
		for (XNode xNode : nodes) {
			try{
				parsePropertiesNode(xNode);
			}catch(Exception e){
				LOGGER.error("could not read configration properties, {0} ignored.", e, e.getMessage());
			}
		}
	}

	private void parsePropertiesNode(XNode context) throws UException {
		if (context == null)
			return;

		String path = context.getAttribute("path");
		String url = context.getAttribute("url");
		boolean hasPath = !StringUtil.isEmptyString(path), 
				hasUrl = !StringUtil.isEmptyString(url);

		if (!hasPath && !hasUrl)
			throw new UException("configuration node properties unexpected, requires at least one of path, url.");
		
		if (hasPath && hasUrl)
			throw new UException("configuration node properties unexpected, one of path, url accepted.");
			
		Properties properties = context.getChildrenAsProperties();
		if (hasPath) {
			properties.putAll(ResourceUtil.getResourceAsProperties(path));
		} else {
			properties.putAll(ResourceUtil.getUrlAsProperties(url));
		}
		
		parser.addVariables(properties);
		configuration.addVariables(properties);
	}
	
	/**
	 * Parses settings node.
	 */
	private void parseSettingsNodes(List<XNode> nodes) throws UException {
		if(nodes == null) return;
		for (XNode xNode : nodes) {
			parseSettingsNode(xNode);
		}
	}

	private void parseSettingsNode(XNode context) throws UException {
		if (context == null)
			return;
		
		String id = context.getAttribute("id");
		String clazz = context.getAttribute("class");
		Object setting = ReflectUtil.instance(clazz);
		
		Properties props = context.getChildrenAsProperties();
		
		if(StringUtil.isEmptyString(id)){
			throw new UException("property id of setting node could not be empty.");
		}
		
		if(StringUtil.isEmptyString(clazz)){
			throw new UException("property class of setting node "+id+" could not be empty.");
		}
		
		Map<String, Method> writers = ReflectUtil.getWriteableMethods(Settings.class);
		for (Iterator<?> iterator = props.keySet().iterator(); iterator.hasNext();) {
			String key = String.valueOf(iterator.next());
			if(!writers.containsKey(key)){
				LOGGER.error("configuration {0} unexpected, property {1} is not supported, ignored.", "settings", key);
			}
		}
		
		ReflectUtil.setProperties(setting, props, true, true);
		configuration.addSetting(id, setting);
	}

}
