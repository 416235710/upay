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
package org.upay.logger.log4j2;

import org.upay.logger.Logger;
import org.upay.logger.LoggerFactory;

/**
 * Log4j2 logger factory.
 * 
 * @version 1.0, 2016-02-08
 * @since Rexdb-1.0
 */
public class Log4j2LoggerFactory extends LoggerFactory {

    protected Logger getLoggerImpl(Class<?> cls) {
        return new Log4j2Logger(org.apache.logging.log4j.LogManager.getLogger(cls));
    }

    protected Logger getLoggerImpl(String name) {
        return new Log4j2Logger(org.apache.logging.log4j.LogManager.getLogger(name));
    }

}
