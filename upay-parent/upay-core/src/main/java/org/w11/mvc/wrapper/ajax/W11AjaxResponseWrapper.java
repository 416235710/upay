package org.w11.mvc.wrapper.ajax;

import javax.servlet.http.HttpServletResponse;

import org.w11.mvc.AjaxView;
import org.w11.mvc.W11Exception;

/**
 * 对Ajax调用方式的封装
 * @author zhouwei
 */
public abstract class W11AjaxResponseWrapper {
	
	private HttpServletResponse response;

	public void init(HttpServletResponse response) {
		this.response=response;
	}
	
	protected HttpServletResponse getResponse() throws W11Exception{
		if(response==null) throw new W11Exception("HttpServletResponse为空，请使用init方法为其赋值");
		return response;
	}
	
	/**
	 * 向客户端发送一个响应
	 */
	public abstract void doResponse(AjaxView service) throws W11Exception;
	
	/**
	 * 向客户端发送一个异常信息
	 */
	public abstract void doException(Throwable t) throws Exception;
	
	/**
	 * 获取执行结果XML
	 */
	public abstract String getResponse(AjaxView service) throws W11Exception;
	
	/**
	 * 获取异常信息
	 */
	public abstract String getException(Throwable t) throws Exception;
}
