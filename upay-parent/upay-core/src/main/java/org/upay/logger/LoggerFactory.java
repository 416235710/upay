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
package org.upay.logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.upay.logger.jdk.JdkLoggerFactory;
import org.upay.logger.log4j.Log4jLoggerFactory;
import org.upay.logger.log4j2.Log4j2LoggerFactory;
import org.upay.logger.slf4j.Slf4jLoggerFactory;

/**
 * Abstract logger factory.
 * 
 * @version 1.0, 2016-02-08
 * @since Rexdb-1.0
 */
public abstract class LoggerFactory {
	
	private static final ReadWriteLock lock = new ReentrantReadWriteLock();
	private static LoggerFactory factory;
	private static volatile boolean nolog = false;
	
	private static final List<LoggerClass> loggers = new LinkedList<LoggerClass>() {
		{
			add(new LoggerClass<Log4jLoggerFactory>("org.apache.commons.logging.LogFactory", Log4jLoggerFactory.class));
			add(new LoggerClass<Slf4jLoggerFactory>("org.slf4j.LoggerFactory", Slf4jLoggerFactory.class));
			add(new LoggerClass<Log4j2LoggerFactory>("org.apache.logging.log4j.LogManager", Log4j2LoggerFactory.class));
		}
	};

	private static void createLoggerFactory() {
		for (LoggerClass logger : loggers) {
			if (logger.isSupported()) {
				factory = logger.createInstance();
				break;
			}
		}
		if (factory == null)
			factory = new JdkLoggerFactory();
	}

	public static Logger getLogger(Class<?> cls) {
		return getLoggerFactory().getLoggerImpl(cls);
	}

	public static Logger getLogger(String name) {
		return getLoggerFactory().getLoggerImpl(name);
	}

	protected static LoggerFactory getLoggerFactory() {
		lock.readLock().lock();
		try {
			if (factory != null)
				return factory;
		} finally {
			lock.readLock().unlock();
		}
		lock.writeLock().lock();
		try {
			if (factory == null) {
				createLoggerFactory();
			}
			return factory;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Disable all logs.
	 */
	public static void setNolog(boolean isNolog){
		getLogger(LoggerFactory.class).info("Logger has switched to {0} mode.", isNolog ? "nolog" : "normal");
		nolog = isNolog;
	}
	
	public static boolean isNolog(){
		return nolog;
	}

	protected abstract Logger getLoggerImpl(Class<?> cls);

	protected abstract Logger getLoggerImpl(String name);

	private static class LoggerClass<T extends LoggerFactory> {

		private final String loggerClazzName;
		private final Class<T> loggerImplClazz;

		public LoggerClass(String loggerClazzName, Class<T> loggerImplClazz) {
			this.loggerClazzName = loggerClazzName;
			this.loggerImplClazz = loggerImplClazz;
		}

		public boolean isSupported() {
			try {
				Class.forName(loggerClazzName);
				return true;
			} catch (ClassNotFoundException ignore) {
				return false;
			}
		}

		public LoggerFactory createInstance() {
			try {
				return loggerImplClazz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("couldn't create loggerFactory instance, " + e.getMessage(), e);
			}
		}
	}

}
