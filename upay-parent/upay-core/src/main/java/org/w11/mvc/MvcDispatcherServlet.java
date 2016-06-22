package org.w11.mvc;

import java.beans.Introspector;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w11.mvc.wrapper.dispatch.Dispatcher;

/**
 * MVC框架控制器
 * 
 * @author zhouwei
 */
public class MvcDispatcherServlet extends HttpServlet {

	public void init() throws ServletException {
		super.init();
		Introspector.flushCaches();
	}

	/**
	 * 执行用户请求
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
		try {
			// 初始请求处理器
			W11CommandProcessor processor = new W11CommandProcessor();
			// 处理请求
			Object view = processor.processRequest(getServletContext(), request, response);
			if (view instanceof View) {
				// 如果是普通请求，返回W11View对象，包含下一步的跳转方式和路径
				String nextPage = ((View) view).getNextPage();
				if (nextPage != null) {
					View v = (View) view;
					if (v.isRedirect()) {
						response.sendRedirect(nextPage);
					} else {
						//将view的值设置到Attribute中
						request.setAttribute(MvcSettings.KEY_VIEW, view);
						if(v != null){
							Iterator iter = v.entrySet().iterator();
							while(iter.hasNext()){
								Map.Entry entry = (Map.Entry)iter.next();
								request.setAttribute(entry.getKey().toString(), entry.getValue());
							}
						}

						//页面跳转
						Dispatcher dispatcher = MvcSettings.getInstance().getDispatcher();
						dispatcher.dispatch(nextPage, request, response, v);
					}
				}
			} else if (view instanceof String) {
				// 如果获得的是String，则认为是Ajax请求，以UTF-8的编码直接输出到页面
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write((String) view);
			}
		} catch (Exception e) {
			try {
				processException(request, response, e);
			} catch (Exception ex) {
				processError(request, response, e, ex);
			}
		}
	}

	// --------------------------------------------以下为异常处理
	/**
	 * 产生异常时，跳转到异常页面
	 */
	private void processException(HttpServletRequest request, HttpServletResponse response, Exception e) {
		Throwable t = e;
		try {
			// if(e instanceof InvocationTargetException) t=e.getCause();
			//
			// MvcSettings settings=MvcSettings.getInstance();
			// String errorPage=settings.getErrorPage();
			// request.setAttribute(MvcSettings.KEY_PAGE_PATH, errorPage);
			// request.setAttribute(MvcSettings.KEY_EXCEPTION, t);

			if (MvcSettings.getInstance().isPrintExceptionOnConsole() && e != null)
				e.printStackTrace();

			request.getRequestDispatcher(MvcSettings.getInstance().getError500()).forward(request, response);
		} catch (Exception t1) {
			processError(request, response, t, t1);
		}
	}

	/**
	 * 产生异常时，如果如果无法跳转到页面，直接输出到页面上
	 */
	private void processError(HttpServletRequest request, HttpServletResponse response, Throwable e, Exception t) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

			response.setContentType("text/html;charset=UTF-8");
			response.setStatus(500);
			PrintWriter writer = response.getWriter();

			writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">");
			writer.write("<HTML><HEAD>");
			writer.write("<META content=\"text/html; charset=UTF-8\" http-equiv=Content-Type>");
			writer.write("<style type=\"text/css\">");
			writer.write("body {background-color: #F1F9FA;}");
			writer.write(
					"#mainTitle {color: #303778;font-family: \"微软雅黑\", \"幼圆\", \"宋体\", \"楷体\" ;font-weight:normal;font-size:20px;margin-left:20px;margin-top:30px;}");
			writer.write("hr {width:70%;margin-left:20px;margin-top:-15px;}");
			writer.write(".title {margin-left:20px;font-size:14px;color: #333333;font-family:\"宋体\",\"微软雅黑\", \"幼圆\",  \"楷体\"; padding-top:5px;}");
			writer.write(".title b {color:#3077AB;}");
			writer.write(".text {font-family: \"宋体\",\"Arial Unicode MS\", \"仿宋\", \"黑体\";font-size:12px;margin-left:20px;}");
			writer.write(
					"#subhead {color:#303778;font-family: \"微软雅黑\", \"幼圆\", \"宋体\", \"楷体\";font-weight:normal;font-size:18px;margin-left:20px;margin-top:30px;}");
			writer.write("</style></HEAD>");
			writer.write("<BODY>");
			writer.write("<H2 id=\"mainTitle\">执行请求时产生了异常</H2>");
			writer.write("<hr align=\"left\" size=\"1\" noshade>");
			writer.write("<DIV class=\"title\"><B>异常信息：</B>");
			writer.write(e.getMessage() == null ? "无特殊信息" : e.getMessage());
			writer.write("</DIV>");
			writer.write("<DIV class=\"title\"><B>异常类：</B>");
			writer.write(e.getClass().getName());
			writer.write("</DIV>");
			writer.write("<DIV class=\"title\"><B>服务器时间：</B>");
			writer.write(sdf.format(Calendar.getInstance().getTime()));
			writer.write("</DIV>");
			writer.write("<div class=\"text\"><PRE>");
			e.printStackTrace(writer);
			writer.write("</PRE></div>");
			writer.write("<H3 id=\"subhead\">同时由于如下异常，无法正常显示友好的错误页面</H3>");
			writer.write("<div class=\"text\"><PRE>");
			t.printStackTrace(writer);
			writer.write("</PRE></div></BODY></HTML>");

		} catch (Exception ignored) {
		}
	}

}
