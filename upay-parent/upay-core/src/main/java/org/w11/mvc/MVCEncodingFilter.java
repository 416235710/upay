package org.w11.mvc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 设置字符集
 * @author zhouwei
 */
public class MVCEncodingFilter implements Filter {

	protected String encoding;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.encoding=filterConfig.getInitParameter("encoding");
		String mvcEncoding=MvcSettings.getInstance().getCharacterEncoding();
		if(mvcEncoding!=null){
			this.encoding=mvcEncoding;
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		String ajaxHeader = req.getHeader("X-Requested-With");
		if (ajaxHeader != null && ajaxHeader.equalsIgnoreCase("XMLHttpRequest")) {
			request.setCharacterEncoding("utf-8");
		} else {
			if (request.getCharacterEncoding() == null && encoding != null) {
				request.setCharacterEncoding(encoding);
				response.setContentType("text/html; charset="+encoding);
			}
		}
		chain.doFilter(request, response);
	}
	
	public void destroy() {
		this.encoding = null;
	}
}
