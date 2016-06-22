/**
 * Copyright 2016 the Rex-Soft Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.upay.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.upay.exception.UException;
import org.upay.logger.Logger;
import org.upay.logger.LoggerFactory;
import org.upay.util.ResourceUtil;

/**
 * Loads XML configuration.
 * 
 * @version 1.0, 2016-02-14
 * @since Rexdb-1.0
 */
public class XMLLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLLoader.class);

	/**
	 * Loads configuration from the file system.
	 * 
	 * @param path absolute path.
	 * @return configuration.
	 * @throws UException could not read or parse the file.
	 */
	public Settings loadFromFileSystem(String path) throws UException {
		File xml = new File(path);
		if (!xml.isFile() || !xml.canRead()) 
			throw new UException("load configuration in the file system "+path+" failed, the file does not exist or is not accessable.");

		try {
			FileInputStream fis = new FileInputStream(xml);
			return load(fis, null);
		} catch (IOException e) {
			throw new UException("load file "+path+" in the file system failed.", e);
		}
	}

	/**
	 * Loads configuration from ClassPath.
	 * 
	 * @param path file path from ClassPath.
	 * @return configuration.
	 * @throws UException could not read or parse the file.
	 */
	public Settings loadFromClasspath(String path) throws UException {
		InputStream inputStream = ResourceUtil.getResourceAsStream(path);
		return load(inputStream, null);
	}

	/**
	 * Loads configuration from InputStream.
	 * 
	 * @param inputStream file InputStream.
	 * @return configuration.
	 * @throws UException could not read or parse the InputStream.
	 */
	public Settings load(InputStream inputStream) throws UException {
		return load(inputStream, null);
	}

	protected Settings load(InputStream inputStream, Properties properties) throws UException {
		XMLParser parser = new XMLParser(inputStream, properties);
		Settings configuration = parser.parse();
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				LOGGER.warn("could not close input stream of xml configuration, {0}.", e.getMessage());
			}
		}
		return configuration;
	}

}
