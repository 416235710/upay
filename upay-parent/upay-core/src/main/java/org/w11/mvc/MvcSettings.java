package org.w11.mvc;

import org.w11.mvc.wrapper.dispatch.BeetlDispatcher;
import org.w11.mvc.wrapper.dispatch.Dispatcher;

public class MvcSettings {
	
	/**
	 * 跳转到页面时，以此做为Attribute的Key，传递view对象
	 */
	public static final String KEY_VIEW="view";
	
	/**
	 * MVC产生异常并跳转到异常页面时，异常以这个Key保存在Attribute里
	 */
	public static final String KEY_EXCEPTION="_exception";
	
	/**
	 * 当command执行完毕，跳转到页面时，以这个Key记录跳转的页面相对路径
	 */
	public static final String KEY_PAGE_PATH="__path";

	
	private static MvcSettings instance = new MvcSettings();
	
	public static MvcSettings getInstance(){
		return instance;
	}
	
	/**
	 * 是否处理上传文件
	 */
	private boolean processUploadFiles = true;
	
	/**
	 * cmd路径省略的前缀
	 */
	private String defaultPackagePrefix="org.upay.merchant";

	/**
	 * 遇到以下前缀开头的URL时，不追加默认前缀
	 */
	private String excludePackagePrefix = "org.w11";
	
	/**
	 * MVC command请求的扩展名
	 */
	private String commandRequestExt;
	
	/**
	 * 使用此name判断调用的是command中的哪个方法
	 */
	private String commandMethodParameterName = "method";
	
	/**
	 * 当没有指定方法名时，使用这个默认方法
	 */
	private String defaultCommandMethod = "index";
	
	/**
	 * 当产生异常时，跳转到此异常页面
	 */
	private String errorPage = "/jsp/error.jsp";
	
	/**
	 * 请求/响应时使用的编码集
	 */
	private String characterEncoding = "UTF-8";
	
	//-----------------------------dispatcher
	
	private Dispatcher dispatcher = new BeetlDispatcher();
	
	private String templateDir = "/tpl";
	
	//----------------------------error
	private boolean printExceptionOnConsole = true;
	private String error404 = "404.html";
	private String error500 = "500.html";
	
	//-------------------------------------getters & setters
	
	public String getDefaultPackagePrefix() {
		return defaultPackagePrefix;
	}

	public boolean isProcessUploadFiles() {
		return processUploadFiles;
	}

	public void setProcessUploadFiles(boolean processUploadFiles) {
		this.processUploadFiles = processUploadFiles;
	}

	public void setDefaultPackagePrefix(String defaultPackagePrefix) {
		this.defaultPackagePrefix = defaultPackagePrefix;
	}

	public String getExcludePackagePrefix() {
		return excludePackagePrefix;
	}

	public void setExcludePackagePrefix(String excludePackagePrefix) {
		this.excludePackagePrefix = excludePackagePrefix;
	}

	public String getCommandMethodParameterName() {
		return commandMethodParameterName;
	}

	public void setCommandMethodParameterName(String commandMethodParameterName) {
		this.commandMethodParameterName = commandMethodParameterName;
	}

	public String getErrorPage() {
		return errorPage;
	}

	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}

	public String getCommandRequestExt() {
		return commandRequestExt;
	}

	public void setCommandRequestExt(String commandRequestExt) {
		this.commandRequestExt = commandRequestExt;
	}

	public String getDefaultCommandMethod() {
		return defaultCommandMethod;
	}

	public void setDefaultCommandMethod(String defaultCommandMethod) {
		this.defaultCommandMethod = defaultCommandMethod;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public Dispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public String getTemplateDir() {
		return templateDir;
	}

	public void setTemplateDir(String templateDir) {
		this.templateDir = templateDir;
	}

	public String getError404() {
		return error404;
	}

	public void setError404(String error404) {
		this.error404 = error404;
	}

	public String getError500() {
		return error500;
	}

	public void setError500(String error500) {
		this.error500 = error500;
	}

	public boolean isPrintExceptionOnConsole() {
		return printExceptionOnConsole;
	}

	public void setPrintExceptionOnConsole(boolean printExceptionOnConsole) {
		this.printExceptionOnConsole = printExceptionOnConsole;
	}
	
}
