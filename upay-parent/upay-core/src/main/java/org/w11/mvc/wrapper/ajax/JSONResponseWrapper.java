package org.w11.mvc.wrapper.ajax;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w11.mvc.AjaxView;
import org.w11.mvc.W11Exception;

import com.alibaba.fastjson.JSONObject;

/**
 * Ajax响应包装类
 */
public class JSONResponseWrapper extends W11AjaxResponseWrapper{

	//格式化
	private DecimalFormat df = new DecimalFormat("#####0.00");
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void setDateFormat(String dateFormat) {
		sdf=new SimpleDateFormat(dateFormat);
	}
	
	/**
	 * 向客户端发送一个异常信息
	 */
	public void doException(Throwable t) throws Exception{

		getResponse().getWriter().write(getException(t));
	}
	
	/**
	 * 获取异常信息
	 */
	public String getException(Throwable t) throws Exception{
		if(t instanceof InvocationTargetException){
			t=t.getCause();
		}
		Map doc=createMap();
//		Map root=createMap();
//		doc.put("response", root);
		doc.put("exception", "true");
		//输出response
		StringWriter sw=new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		doc.put("message", sw.getBuffer().toString());
		return jsonToString(doc);
	}
	
	/**
	 * 向客户端发送一个响应,该响应仅包含结果信息，不包含请求参数信息
	 */
	public void doResponse(AjaxView view) throws W11Exception{
		try {
			//20150317 如果具有Grid对象，则按照Grid格式输出
			getResponse().getWriter().write(toJson(view));
		} catch (Exception e) {
			throw new W11Exception("输出JSON时,服务器输出流产生了异常:"+e.getMessage(),e);
		} 
	}
	
	/**
	 * 获取执行结果json
	 */
	public String getResponse(AjaxView view) throws W11Exception{
		try {
			return toJson(view);
		} catch (Exception e) {
			throw new W11Exception("获取JSON时,服务器输出流产生了异常:"+e.getMessage(),e);
		} 
	}
	
	//将参数输入到json（标准Ajax模式，带有结果状态等信息）
	protected String toJson(AjaxView view) throws W11Exception{
		try{
//			Map doc=createMap();
//			Map root=createMap();
//			doc.put("response", root);
//			
//			root.put("error",String.valueOf(view.hasError()));
//			root.put("errorCode", view.getErrorCode()==null?"":view.getErrorCode());
//			root.put("errorMsg", view.getErrorMsg()==null?"":view.getErrorMsg());
			Map response=createMap();
//			root.put("response", response);
			
			if(view.hasError()){
				response.put("error",String.valueOf(view.hasError()));
				response.put("errorCode", view.getErrorCode()==null?"":view.getErrorCode());
				response.put("errorMsg", view.getErrorMsg()==null?"":view.getErrorMsg());
			}else
				writeToJson(response,view);
			
			//输出
			return jsonToString(response);
		}catch(Exception e){
			throw new W11Exception("构建JSON对象时产生了异常："+e.getMessage(),e);
		}
	}
	
	/**
	 * 创建json map容器
	 */
	public Map createMap() {
		return new LinkedHashMap();
	}
	
	/**
	 * 把json作为字符串输出
	 */
	public String jsonToString(Map map){
		return JSONObject.toJSONString(map);
	}
	
	/**
	 * 将Map中内容写入json
	 */
	protected void writeToJson(Map response,Map results) throws Exception{
		Iterator iter=results.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			String key=(String)entry.getKey();
			Object value=entry.getValue();
			//创建节点
			response.putAll(createNode(key,value));
		}
	}
	
	/**
	 * 创建节点，自身递归以适应各种类型的值
	 */
	public Map createNode(String key,Object value) throws Exception{
		Map node=createMap();
		//值是普通类型,Date型的话format一下
		if(value==null){
			node.put(key, "");
		}else if(value instanceof Date){
			node.put(key, sdf.format((Date)value));
		}else if(value instanceof String|| value instanceof Integer||value instanceof Long || value instanceof Boolean){
			node.put(key, value.toString());
		}else if(value instanceof Double || value instanceof Float || value instanceof BigDecimal){
			node.put(key, df.format(value));
		//值是Map类型
		}
		//值是数组
		else if(value.getClass().isArray()){
			Object[] values = (Object[])value;
			List l=new ArrayList();
			for(int i=0;i<values.length;i++){
				l.add(values[i]);
			}
			node.put(key,l);
		}
		//值是Map类型
		else if(value instanceof Map){
			Map entrys = new HashMap();
			Iterator iter=((Map)value).entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry=(Map.Entry)iter.next();
				entrys.putAll(createNode((String)entry.getKey(),entry.getValue()));
			}
			node.put(key, entrys);
		//值是List类型
		}else if(value instanceof List){
			List l=(List)value;
			for(int i=0;i<l.size();i++){
				Map m = createNode("key", l.get(i));
				l.set(i, m.get("key"));
			}
			node.put(key,l);
			
		}
		//最后一概当成pojo类处理
		else{
			Map entrys=new LinkedHashMap();
			BeanInfo bean=Introspector.getBeanInfo(value.getClass());
			PropertyDescriptor[] props=bean.getPropertyDescriptors();
			for(int i=0;i<props.length;i++){
				Method reader=props[i].getReadMethod();
				if(reader!=null){
					Object propValue=reader.invoke(value, null);
					if(propValue==null||propValue instanceof Date||propValue instanceof String|| propValue instanceof Integer||
							propValue instanceof Long ||propValue instanceof Double	||propValue instanceof Float ||
							propValue instanceof Boolean || propValue instanceof BigDecimal||propValue instanceof Map||
							propValue instanceof List){
						entrys.putAll(createNode(props[i].getName(),propValue));
					}
				}
			}
			
			node.put(key, entrys);
		}
		
		return node;
	}
}
