package org.w11.mvc.wrapper.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.DefaultFileItem;
import org.apache.commons.fileupload.FileItem;

/**
 * 上传文件接口实现类
 * 文件上传使用Apache Common File Upload实现
 */
public class CommonsMultipartFile implements MultipartFile {

	private final FileItem fileItem;

	protected CommonsMultipartFile(FileItem fileItem) {
		this.fileItem = fileItem;
	}

	public FileItem getFileItem() {
		return fileItem;
	}

	public String getName() {
		return this.fileItem.getFieldName();
	}

	public String getOriginalFileName() {
		return new File(this.fileItem.getName()).getName();
	}

	public String getContentType() {
		return this.fileItem.getContentType();
	}

	public long getSize() {
		return this.fileItem.getSize();
	}

	public byte[] getBytes() {
		return this.fileItem.get();
	}

	public InputStream getInputStream() throws IOException {
		return this.fileItem.getInputStream();
	}

	public void saveAs(File dest) throws IOException, IllegalStateException {
		try {
			this.fileItem.write(dest);
		}catch (Exception ex) {
			throw new IOException("无法将文件另存至另一位置："+
					"\n文件名：" +getOriginalFileName() +
					"\n表单域：" + getName() +
					"\n原位置：" + getStorageDescription() +
					"\n新位置：" + dest.getAbsolutePath()+
					"\n异常信息："+ex.getMessage());
		}
	}

	protected String getStorageDescription() {
		if (this.fileItem.isInMemory())
			return "内存";
		else if (this.fileItem instanceof DefaultFileItem)
			return " [" + ((DefaultFileItem) fileItem).getStoreLocation().getAbsolutePath() + "]";
		else
			return "硬盘";
	}

}
