package org.w11.mvc.wrapper.fileupload;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Abstract上传文件包装类
 */
public abstract class AbstractMultipartHttpServletRequest extends HttpServletRequestWrapper
    implements MultipartHttpServletRequest {

	private Map multipartFiles;

	protected AbstractMultipartHttpServletRequest(HttpServletRequest request) {
		super(request);
	}

	protected void setMultipartFiles(Map multipartFiles) {
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
}
