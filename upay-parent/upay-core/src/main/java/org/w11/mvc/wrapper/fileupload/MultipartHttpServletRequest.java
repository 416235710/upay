package org.w11.mvc.wrapper.fileupload;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传Request包装类
 */
public interface MultipartHttpServletRequest extends HttpServletRequest {

	Iterator getFileNames();

	MultipartFile getFile(String name);

	Map getFileMap();
}
