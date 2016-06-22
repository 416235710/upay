package org.w11.mvc;

import java.util.Iterator;
import java.util.Map;

import org.rex.RMap;
import org.w11.mvc.wrapper.fileupload.MultipartFile;

/**
 * View接口
 * @author zhouwei jizhq
 */
public class View extends RMap {
	
	//嵌套模板
	public static final String MAINFRAME_NULL = null;
	
	public static final String MAINFRAME_INDEX = "main.html";
	
	//通知级别
	public static final String NOTIFY_LEVEL_NORMAL = "";
	
	public static final String NOTIFY_LEVEL_WARN = "warn";
	
	//是否以dispatch的方式跳转
	private boolean isRedirect=false;
	
	//模板页面
	private String mainFrame = MAINFRAME_INDEX;
	
	//执行完command方法之后，跳转至哪一个页面
	private String nextPage;
	
	//----------------------上传文件
	private Map multipartFiles;
	
	public void setMultipartFiles(Map multipartFiles) {
		this.multipartFiles = multipartFiles;
	}
	
	public Iterator getFileNames() {
		return this.multipartFiles.keySet().iterator();
	}

	public MultipartFile getFile(String name) {
		return (MultipartFile) this.multipartFiles.get(name);
	}

	public Map getFileMap() {
		return this.multipartFiles;
	}
	
	public boolean isRedirect() {
		return isRedirect;
	}

	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public String getNextPage() {
		return nextPage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}

	public String getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(String mainFrame) {
		this.mainFrame = mainFrame;
	}

	//--NOTIFY
	public String getNotifyTitle() {
		return (String)get("notifyTitle");
	}

	public void setNotifyTitle(String notifyTitle) {
		this.put("notifyTitle", notifyTitle);
	}

	public String getNotifyText() {
		return (String)get("notifyText");
	}

	public void setNotifyText(String notifyText) {
		this.put("notifyText", notifyText);
	}

	public String getNotifyLevel() {
		return (String)get("notifyLevel");
	}

	public void setNotifyLevel(String notifyLevel) {
		this.put("notifyLevel", notifyLevel);
	}
	
}
