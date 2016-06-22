package org.w11.mvc.wrapper.fileupload;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.w11.mvc.W11Exception;

/**
 * 解析文件上传表单实现类
 * 使用Apache Common File Upload包
 */
public class CommonsMultipartResolver implements MultipartResolver {

	private DiskFileUpload fileUpload;

	public CommonsMultipartResolver() {
		this.fileUpload = newFileUpload();
	}

	public CommonsMultipartResolver(ServletContext servletContext) {
		this();
		this.fileUpload.setRepositoryPath(((File) servletContext
				.getAttribute("javax.servlet.context.tempdir")).getAbsolutePath());
	}

	protected DiskFileUpload newFileUpload() {
		return new DiskFileUpload();
	}

	public DiskFileUpload getFileUpload() {
		return fileUpload;
	}

	public void setMaximumFileSize(long maximumFileSize) {
		this.fileUpload.setSizeMax(maximumFileSize);
	}

	public void setMaximumInMemorySize(int maximumInMemorySize) {
		this.fileUpload.setSizeThreshold(maximumInMemorySize);
	}

	public void setHeaderEncoding(String headerEncoding) {
		this.fileUpload.setHeaderEncoding(headerEncoding);
	}

	public boolean isMultipart(HttpServletRequest request) {
		return FileUploadBase.isMultipartContent(request);
	}

	public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws W11Exception {
		try {
			//--处理post参数
			List fileItems = this.fileUpload.parseRequest(request);
			Map parameters = new HashMap();
			Map multipartFiles = new HashMap();
			for (Iterator i = fileItems.iterator(); i.hasNext();) {
				FileItem fileItem = (FileItem) i.next();
				if (fileItem.isFormField()) {
					String[] curParam = (String[]) parameters.get(fileItem.getFieldName());
					if (curParam == null) {
						parameters.put(fileItem.getFieldName(), new String[] { fileItem.getString() });
					}
					else {
						String[] newParam = addStringToArray(curParam, fileItem.getString());
						parameters.put(fileItem.getFieldName(), newParam);
					}
				}
				else {
					CommonsMultipartFile file = new CommonsMultipartFile(fileItem);
					multipartFiles.put(file.getName(), file);
				}
			}
			
			//--处理get参数
			parameters.putAll(request.getParameterMap());
			return new DefaultMultipartHttpServletRequest(request, multipartFiles, parameters);
		}
		catch (FileUploadException ex) {
			throw new W11Exception("使用Apache Common File Upload包解析上传文件时产生异常", ex);
		}
	}

	public void cleanupMultipart(MultipartHttpServletRequest request) {
		Map multipartFiles = request.getFileMap();
		for (Iterator i = multipartFiles.keySet().iterator(); i.hasNext();) {
			String name = (String) i.next();
			CommonsMultipartFile file = (CommonsMultipartFile) multipartFiles.get(name);
			file.getFileItem().delete();
		}
	}
	
	public static String[] addStringToArray(String[] arr, String s) {
		String[] newArr = new String[arr.length + 1];
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		newArr[arr.length] = s;
		return newArr;
	}

}
