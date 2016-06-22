package org.w11;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w11.mvc.AjaxView;
import org.w11.mvc.MvcSettings;
import org.w11.mvc.View;
import org.w11.mvc.W11Exception;
import org.w11.mvc.wrapper.ajax.JSONPResponseWrapper;
import org.w11.mvc.wrapper.ajax.JSONResponseWrapper;
import org.w11.mvc.wrapper.ajax.W11AjaxResponseWrapper;
import org.w11.mvc.wrapper.ajax.W11AjaxXMLResponseWrapper;

/**
 * W11 Command执行入口
 * 
 * @author zhouwei
 */
public class Command {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Command执行入口
	 */
	public Object execute(HttpServletRequest request, HttpServletResponse response, View view) throws Exception {

		String methodName = view.getString(MvcSettings.getInstance().getCommandMethodParameterName());
		// 使用默认方法
		if (methodName == null || "".equals(methodName)) {
			methodName = MvcSettings.getInstance().getDefaultCommandMethod();
		}

		Method method = null;

		// 执行普通方法
		method = getCommonMethod(methodName);
		if (method != null) {
			Object o = method.invoke(this, new Object[] { request, response, view });
			String nextPage = o == null ? null : o.toString();
			view.setNextPage(nextPage);
			return view;
		}
		// 执行Ajax方法
		method = getAjaxMethod(methodName);
		if (method != null) {
			W11AjaxResponseWrapper wrapper = new JSONResponseWrapper();
			try {
				AjaxView ajaxView = new AjaxView(view);
				method.invoke(this, new Object[] { request, response, ajaxView });
				// 根据情况记录错误信息
				// TrackException.trackAjaxWarn(request, ajaxView);
				// String responseType=request.getHeader("Response-Type");
				String responseType = ajaxView.getResponseType();
				if ("xml".equalsIgnoreCase(responseType)) {
					wrapper = new W11AjaxXMLResponseWrapper();
				} else if ("jsonp".equalsIgnoreCase(responseType)) {
					wrapper = new JSONPResponseWrapper();
				}

				return wrapper.getResponse(ajaxView);
			} catch (Exception e) {
				return wrapper.getException(e);
			}
		}

		// 都没执行，抛异常
		throw new W11Exception("未能找到适当的方法处理请求：" + this.getClass().getName() + " , " + methodName);
	}

	/**
	 * 获取常规的Command方法
	 */
	public Method getCommonMethod(String methodName) {
		Method method = getAccessibleMethod(this.getClass(), methodName,
				new Class[] { HttpServletRequest.class, HttpServletResponse.class, View.class });
		if (method != null) {
			return method;
		}
		return null;
	}

	/**
	 * 获取Ajax方法
	 */
	public Method getAjaxMethod(String methodName) {
		return getAccessibleMethod(this.getClass(), methodName, new Class[] { HttpServletRequest.class, HttpServletResponse.class, AjaxView.class });
	}

	// ---------------以下为查找合适方法的代码
	public static Method getAccessibleMethod(Class clazz, String methodName, Class parameterType) {

		Class[] parameterTypes = { parameterType };
		return getAccessibleMethod(clazz, methodName, parameterTypes);
	}

	public static Method getAccessibleMethod(Class clazz, String methodName, Class[] parameterTypes) {

		try {
			return getAccessibleMethod(clazz.getMethod(methodName, parameterTypes));
		} catch (NoSuchMethodException e) {
			return (null);
		}
	}

	public static Method getAccessibleMethod(Method method) {
		if (method == null) {
			return (null);
		}
		if (!Modifier.isPublic(method.getModifiers())) {
			return (null);
		}
		Class clazz = method.getDeclaringClass();
		if (Modifier.isPublic(clazz.getModifiers())) {
			return (method);
		}
		method = getAccessibleMethodFromInterfaceNest(clazz, method.getName(), method.getParameterTypes());
		return (method);
	}

	private static Method getAccessibleMethodFromInterfaceNest(Class clazz, String methodName, Class parameterTypes[]) {
		Method method = null;
		for (; clazz != null; clazz = clazz.getSuperclass()) {
			Class interfaces[] = clazz.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				if (!Modifier.isPublic(interfaces[i].getModifiers())) {
					continue;
				}
				try {
					method = interfaces[i].getDeclaredMethod(methodName, parameterTypes);
				} catch (NoSuchMethodException e) {
				}
				if (method != null) {
					break;
				}
				method = getAccessibleMethodFromInterfaceNest(interfaces[i], methodName, parameterTypes);
				if (method != null) {
					break;
				}
			}
		}
		if (method != null) {
			return (method);
		} else {
			return (null);
		}
	}
}
