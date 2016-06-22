package org.upay.setting;

/**
 * 通用模块的配置
 * 
 * @author z
 */
public class CommonSetting {

	/**
	 * 服务配置文件
	 */
	private String serviceXML;

	/**
	 * 客户端配置文件
	 */
	private String clientXML;

	public String getServiceXML() {
		return serviceXML;
	}

	public void setServiceXML(String serviceXML) {
		this.serviceXML = serviceXML;
	}

	public String getClientXML() {
		return clientXML;
	}

	public void setClientXML(String clientXML) {
		this.clientXML = clientXML;
	}

}
