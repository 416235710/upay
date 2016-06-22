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
package org.upay.setting.xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.upay.exception.UException;
import org.upay.logger.Logger;
import org.upay.logger.LoggerFactory;
import org.upay.util.ResourceUtil;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML entity resolver.
 *
 * @version 1.0, 2016-02-14
 * @since Rexdb-1.0
 */
public class XEntityResolver implements EntityResolver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XEntityResolver.class);

	private static final Map<String, String> doctypeMap = new HashMap<String, String>();

	private static final String CONFIG_PUBLIC = "-//UPAY//UPAY SETTINGS DTD 1.0//EN".toUpperCase(Locale.ENGLISH);
	private static final String CONFIG_DTD = "org/upay/setting/uapy-settings.dtd";

	static {
		doctypeMap.put(CONFIG_PUBLIC, CONFIG_DTD);
	}

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException {

		if (publicId != null) {
			publicId = publicId.toUpperCase(Locale.ENGLISH);
		}
		if (systemId != null) {
			systemId = systemId.toUpperCase(Locale.ENGLISH);
		}

		InputSource source = null;
		try {
			String path = doctypeMap.get(publicId);
			source = getInputSource(path, source);
			if (source == null) {
				path = doctypeMap.get(systemId);
				source = getInputSource(path, source);
			}
		} catch (Exception e) {
			throw new SAXException(e.toString());
		}
		return source;
	}

	private InputSource getInputSource(String path, InputSource source) {
		if (path != null) {
			InputStream in;
			try {
				in = ResourceUtil.getResourceAsStream(path);
				source = new InputSource(in);
			} catch (UException e) {
				LOGGER.warn("failed to load dtd file {0}, {1}.", e, path, e.getMessage());
			}
		}
		return source;
	}

}