package org.w11.mvc.wrapper.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *上传文件接口
 */
public interface MultipartFile {

	String getName();

	String getOriginalFileName();

	String getContentType();

	long getSize();

	byte[] getBytes() throws IOException;

	InputStream getInputStream() throws IOException;

	void saveAs(File dest) throws IOException, IllegalStateException;
}
