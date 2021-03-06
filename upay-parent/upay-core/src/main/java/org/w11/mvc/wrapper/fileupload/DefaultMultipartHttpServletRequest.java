package org.w11.mvc.wrapper.fileupload;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传Request包装实现类
 */
public class DefaultMultipartHttpServletRequest extends AbstractMultipartHttpServletRequest {

	private Map parameters;

	public DefaultMultipartHttpServletRequest(HttpServletRequest request, Map multipartFiles, Map parameters) {
		super(request);
		setMultipartFiles(multipartFiles);
		this.parameters = Collections.unmodifiableMap(parameters);
	}

	public Enumeration getParameterNames() {
		return Collections.enumeration(this.parameters.keySet());
	}

	public String getParameter(String name) {
		String[] values = getParameterValues(name);
		return (values != null && values.length > 0 ? values[0] : null);
	}

	public String[] getParameterValues(String name) {
		return (String[]) this.parameters.get(name);
	}

	public Map getParameterMap() {
		return this.parameters;
	}

}
