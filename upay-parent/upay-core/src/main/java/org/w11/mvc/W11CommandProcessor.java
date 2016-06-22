package org.w11.mvc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w11.Command;
import org.w11.mvc.wrapper.fileupload.AbstractMultipartHttpServletRequest;
import org.w11.mvc.wrapper.fileupload.CommonsMultipartResolver;
import org.w11.mvc.wrapper.fileupload.MultipartResolver;

/**
 * 请求处理器，有多个实现
 * @author zhw
 */
public class W11CommandProcessor {

	/**
	 * 处理请求
	 */
	public Object processRequest(ServletContext servletContext,
			HttpServletRequest request,HttpServletResponse response) throws Exception{
		View view=wrapView(request);
		String CommandName = getCommandName(request.getServletPath());
		
		Command command =W11CommandFactory.getInstance().getCommand(CommandName);
		return command.execute(request, response, view);
	}
	
	/**
	 * 包装请求参数
	 */
	public View wrapView(HttpServletRequest request) throws W11Exception{
		View view=new View();
		
		if(MvcSettings.getInstance().isProcessUploadFiles()){
			request=wrapMultipartRequest(request);
			//--表示上传了文件
			if(request instanceof AbstractMultipartHttpServletRequest){
				view.setMultipartFiles(((AbstractMultipartHttpServletRequest)request).getFileMap());
			}
		}
		populateMap(view,request.getParameterMap());
		return view;
	}

	
	
	/**
	 * 从字符串中获取command的名称,比如(/.../.../Foo.cmd -> Foo)
	 * 
	 * @param name字符串
	 */
	protected String getCommandName(String name) {
		int beginIdx = name.lastIndexOf("/");
		int endIdx = name.lastIndexOf(".");//默认最后一个'.'后的是扩展名
		return name.substring((beginIdx == -1 ? 0 : beginIdx + 1),
				endIdx == -1 ? name.length() : endIdx);
	}
	
	/**
	 * 检查请求是否上传了文件，并对其进行包装
	 */
	protected HttpServletRequest wrapMultipartRequest(HttpServletRequest request)
			throws W11Exception {
		MultipartResolver resolver = new CommonsMultipartResolver();
		if (resolver.isMultipart(request))
			return resolver.resolveMultipart(request);
		else
			return request;
	}
	
	/**
	 * 为View赋值
	 */
	protected void populateMap(Map view,Map parameterMap){
		List<String> params=new ArrayList<String>();
		//1.整理数据
		for(Iterator iter = parameterMap.entrySet().iterator(); iter.hasNext();){
			Map.Entry entry=(Map.Entry)iter.next();
			String key=(String)entry.getKey();
			Object value=entry.getValue();
			if(value==null){
				view.put(key, null);
			}else {
				if(value.getClass().isArray()) {
					int length=((Object[])value).length;
					if(length==1) {
						view.put(key, ((Object[])value)[0]);
					}else{
						view.put(key, value);
					}
				}else{
					view.put(key, value);
				}
			}
		}
	}
}
