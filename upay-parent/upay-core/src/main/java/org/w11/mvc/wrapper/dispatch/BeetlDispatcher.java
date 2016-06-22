package org.w11.mvc.wrapper.dispatch;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.ext.servlet.ServletGroupTemplate;
import org.w11.mvc.MvcSettings;
import org.w11.mvc.View;

public class BeetlDispatcher implements Dispatcher {

	public void dispatch(String nextPage, HttpServletRequest request, HttpServletResponse response, View view) throws IOException {

		// 1.没有返回页面时
		if (nextPage == null)
			return;

		// 2.跳转到下一个页面
		String tpl;
		MvcSettings settings = MvcSettings.getInstance();
		tpl = settings.getTemplateDir() + '/' + nextPage;

		if (view.getMainFrame() == view.MAINFRAME_NULL) {
			tpl = settings.getTemplateDir() + '/' + nextPage;
		} else {
			request.setAttribute(MvcSettings.KEY_PAGE_PATH, nextPage);
			tpl = settings.getTemplateDir() + '/' + view.getMainFrame();
		}
		tpl = tpl.replaceAll("//", "/");

		// 3. 计算模板页面相对路径
		String basePath = settings.getTemplateDir() + '/' + nextPage.substring(0, nextPage.lastIndexOf("/"));
		request.setAttribute("basePath", basePath.indexOf("/") != -1 ? basePath.substring(1) : basePath);

		response.setContentType("text/html;charset=UTF-8");
		ServletGroupTemplate.instance().render(tpl, request, response);
	}
}
