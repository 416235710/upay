package org.w11.mvc.wrapper.fileupload;

import javax.servlet.http.HttpServletRequest;

import org.w11.mvc.W11Exception;

/**
 * 解析文件上传表单
 */
public interface MultipartResolver {

	public boolean isMultipart(HttpServletRequest request);

	public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws W11Exception;

	public void cleanupMultipart(MultipartHttpServletRequest request);
}
