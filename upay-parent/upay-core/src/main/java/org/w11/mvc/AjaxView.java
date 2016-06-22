package org.w11.mvc;

import org.rex.RMap;

/**
 * Ajax通用处理类
 * @author zhouwei
 */
public class AjaxView extends RMap{

	//结果数据封装格式
	private String responseType = "json";
	
	//jsonp callback函数名，不设置使用该参数作为函数名
	private String jsonpCallback = "callback";
	
	// 是否出错
	private boolean hasError;
	// 出错信息
	private String errorMsg;
	//错误号
	private String errorCode;
	
	public AjaxView(RMap params){
		String paramCallback = params.getString(jsonpCallback);
		if(paramCallback != null && !"".equals(paramCallback.trim()))
			jsonpCallback = paramCallback;
		
		this.putAll(params);
	}
	
	//----------------------responseType
	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	
	public String getJsonpCallback() {
		return jsonpCallback;
	}

	public void setJsonpCallback(String jsonpCallback) {
		this.jsonpCallback = jsonpCallback;
	}

	//----------------------处理一般数据
	public boolean hasError() {
		return hasError;
	}
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
		setHasError(true);
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
		setHasError(true);
	}
	public void setError(String errorCode, String errorMsg){
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		setHasError(true);
	}
	
}
