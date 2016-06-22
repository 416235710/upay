package org.upay.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.upay.exception.URuntimeException;
import org.upay.logger.Logger;
import org.upay.logger.LoggerFactory;
import org.upay.service.sample.SampleService;
import org.upay.setting.CommonSetting;
import org.upay.setting.Settings;
import org.upay.util.StringUtil;

/**
 * Dubbo Services Manager
 * @author z
 */
public class DubboManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DubboManager.class);
	
	public static final String SETTING_ID = "common";

	static ClassPathXmlApplicationContext serviceContext;
	
	static ClassPathXmlApplicationContext clientContext;
	 
	//-------starts & closes context
	/**
	 * Initializes service context.
	 */
	public synchronized static void startContext() {
		try{
			startService();
		}catch(Exception e){
			LOGGER.error("Error on starting dubbo service context.", e);
		}
		
		try{
			startClient();
		}catch(Exception e){
			LOGGER.error("Error on starting dubbo client context.", e);
		}
	}
	
	
	
	/**
	 * Closes service context.
	 */
	public synchronized static void closeContext() {
		try{
			closeService();
		}catch(Exception e){
			LOGGER.error("Error on closing dubbo service context.", e);
		}
		
		try{
			closeClient();
		}catch(Exception e){
			LOGGER.error("Error on closing dubbo client context.", e);
		}
	}
	
	//-------client
	/**
	 * Returns dubbo services if exists.
	 * @param id
	 * @return
	 */
	public static Object getService(String id){
		if(clientContext == null){
			throw new URuntimeException("Dubbo instance is not initialized.");
		}
 
        return clientContext.getBean(id);
	}
	
	
	//-------private
	//---service
	private static void startService(){
		CommonSetting setting = getSetting();
		if(setting == null){
			LOGGER.warn("Setting 'common' not found, could not create dubbo service instance.");
			return;
		}
		
		String serviceXML = setting.getServiceXML();
		if(StringUtil.isEmptyString(serviceXML)){
			LOGGER.warn("Property 'serviceXML' is empty, could not create dubbo service instance.");
			return;
		}
		
		LOGGER.info("Starting dubbo service instance.");
		
		serviceContext = new ClassPathXmlApplicationContext(serviceXML);
		serviceContext.start();
		
		LOGGER.info("Dubbo service instance started by {0}.", serviceXML);
	}
	
	private static void closeService(){
		LOGGER.info("Closing dubbo service instance.");
		serviceContext.close();
		LOGGER.info("Dubbo service instance closed.");
	}
	
	//----client
	private static void startClient(){
		CommonSetting setting = getSetting();
		if(setting == null){
			LOGGER.warn("Setting 'common' not found, could not create dubbo client instance.");
			return;
		}
		
		String clientXML = setting.getClientXML();
		if(StringUtil.isEmptyString(clientXML)){
			LOGGER.warn("Property 'clientXML' is empty, could not create dubbo client instance.");
			return;
		}
		
		LOGGER.info("Starting dubbo client instance.");
		
		clientContext = new ClassPathXmlApplicationContext(clientXML);
		clientContext.start();
		
		LOGGER.info("Dubbo client instance started by {0}.", clientXML);
	}
	
	private static void closeClient(){
		LOGGER.info("Closing dubbo client instance.");
		clientContext.close();
		LOGGER.info("Dubbo client instance closed.");
	}
	
	private static CommonSetting getSetting(){
		return (CommonSetting)Settings.getCurrentSettings().getSetting(SETTING_ID);
	}
	
	//---test client
	public static void main(String[] args) {
		DubboManager.startClient();
		SampleService service = (SampleService)DubboManager.getService("client-sampleService");
		System.out.println("---"+service.getName());
	}
}
