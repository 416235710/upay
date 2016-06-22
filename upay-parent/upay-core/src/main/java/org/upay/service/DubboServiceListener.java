package org.upay.service;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Starts Duboo services.
 * @author z
 */
public class DubboServiceListener implements ServletContextListener{
	
	ClassPathXmlApplicationContext context;

	/**
	 * Initialize services.
	 */
	public void contextInitialized(ServletContextEvent event) {
		DubboManager.startContext();
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		DubboManager.closeContext();
	}
	
	//---test service
	public static void main(String[] args) throws IOException {
		DubboServiceListener d = new DubboServiceListener();
		d.contextInitialized(null);
		
		System.in.read();
	}
}
