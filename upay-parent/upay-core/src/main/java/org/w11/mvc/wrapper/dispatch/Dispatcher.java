package org.w11.mvc.wrapper.dispatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w11.mvc.View;

/**
 * 提交到展示页面
 */
public interface Dispatcher {

	public void dispatch(String file, HttpServletRequest request, HttpServletResponse response, View view) throws Exception;
}
