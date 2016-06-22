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
package org.upay.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * String utilities.
 * 
 * @version 1.0, 2016-01-29
 * @since Rexdb-1.0
 */
public class StringUtil {

	/**
	 * Returns true if the given String is null or ''.
	 */
	public static boolean isEmptyString(String s){
		return s == null || "".equals(s.trim());
	}
	
	/**
	 * Formats String with the given arguments.
	 * sample: format("Hello {0}!", "World"); -> Hello World
	 */
	public static String format(String message, Object... params) {
		if(params != null && params.length > 0)
			return MessageFormat.format(message, params);
		else
			return message;
	}
	
	/**
	 * Splits the given string and removes the empty elements.
	 */
	public static String[] split(String s, String r){
		if(s == null || r == null) return null;
		
		String[] a = s.split(r);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < a.length; i++) {
			if(!isEmptyString(a[i])){
				list.add(a[i].trim());
			}
		}
		
		return list.toArray(new String[list.size()]);
	}
}
