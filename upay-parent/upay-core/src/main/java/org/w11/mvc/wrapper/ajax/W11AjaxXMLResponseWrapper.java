package org.w11.mvc.wrapper.ajax;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w11.mvc.AjaxView;
import org.w11.mvc.W11Exception;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Ajax响应包装类
 * @author zhouwei
 */
public class W11AjaxXMLResponseWrapper extends W11AjaxResponseWrapper{

	//xml编码
	private String xmlEncoding="UTF-8";
	//date格式化
	private String dateFormat="yyyy-MM-dd hh:mm:ss";
	
	public String getXmlEncoding() {
		return xmlEncoding;
	}

	public void setXmlEncoding(String xmlEncoding) {
		this.xmlEncoding = xmlEncoding;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
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
		
		Document doc = createLiteRoot();
		Element root = doc.createElement("w11");
		doc.appendChild(root);

		//输出response
		Element responseEl = doc.createElement("response");
		responseEl.setAttribute("exception","true");
		StringWriter sw=new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		Text ex=doc.createTextNode(sw.getBuffer().toString());
		responseEl.appendChild(ex);
		root.appendChild(responseEl);
		
		return xmlToString(doc);
	}
	
	/**
	 * 向客户端发送一个响应,该响应仅包含结果信息，不包含请求参数信息
	 */
	public void doResponse(AjaxView view) throws W11Exception{
		try {
			getResponse().getWriter().write(toXml(view));
		} catch (IOException e) {
			throw new W11Exception("输出XML时,服务器输出流产生了异常:"+e.getMessage(),e);
		}
	}
	
	/**
	 * 获取执行结果XML
	 */
	public String getResponse(AjaxView service) throws W11Exception{
		return toXml(service);
	}

	//将参数输入到XML
	protected String toXml(AjaxView view) throws W11Exception{
		try{
			Document doc = createLiteRoot();
			Element root = doc.createElement("w11");
			doc.appendChild(root);
			
			//输出response
			Element responseEl = doc.createElement("response");
			responseEl.setAttribute("error",String.valueOf(view.hasError()));
			responseEl.setAttribute("errorMsg", view.getErrorMsg()==null?"":view.getErrorMsg());
			
			root.appendChild(responseEl);
			writeToXml(doc,responseEl,view);
			
			//输出
			return xmlToString(doc);
		}catch(Exception e){
			throw new W11Exception("构建XML对象时产生了异常："+e.getMessage(),e);
		}
	}
	
	/**
	 * 创建taxlite xml根节点
	 */
	public Document createLiteRoot() throws ParserConfigurationException, FactoryConfigurationError{
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return db.newDocument();
	}
	
	/**
	 * 把xml作为字符串输出
	 */
	public String xmlToString(Document doc) throws TransformerException{
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, xmlEncoding);
		StringWriter writer = new StringWriter();   
		StreamResult sResult = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), sResult);   
		return writer.toString();
	}
	
	/**
	 * 将Map中内容写入XML
	 */
	protected void writeToXml(Document doc,Element el,Map params) throws DOMException, Exception{
		
		Map resp=params;
		Iterator iter=resp.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry=(Map.Entry)iter.next();
			String key=(String)entry.getKey();
			Object value=entry.getValue();
			//创建xml节点
			el.appendChild(createNode(doc,key,value));
		}
	}
	
	/**
	 * 创建xml节点，自身递归以适应各种类型的值
	 */
	public Node createNode(Document doc,String key,Object value) throws Exception{
		Element node=null;
		//值是普通类型,Date型的话format一下
		if(value==null){
			node = doc.createElement("param");
			node.setAttribute("key",key);
			node.appendChild(doc.createTextNode(""));
		}else if(value instanceof Date){
			SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
			node = doc.createElement("param");
			node.setAttribute("key",key);
			node.appendChild(doc.createTextNode(sdf.format((Date)value)));
		}else if(value instanceof String|| value instanceof Integer||value instanceof Long ||value instanceof Double
				||value instanceof Float ||value instanceof Boolean || value instanceof BigDecimal){
			node = doc.createElement("param");
			node.setAttribute("key",key);
			node.appendChild(doc.createTextNode(value.toString()));
		}
		//值是Map类型
		else if(value instanceof Map){
			node=doc.createElement("bean");
			node.setAttribute("key", key);
			node.setAttribute("class", value.getClass().getName());
			
			Iterator iter=((Map)value).entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry=(Map.Entry)iter.next();
				node.appendChild(createNode(doc,(String)entry.getKey(),entry.getValue()));
			}
		//值是List类型
		}else if(value instanceof List){
			node=doc.createElement("list");
			node.setAttribute("key", key);
			
			List valueList=(List)value;
			for(int i=0;i<valueList.size();i++){
				node.appendChild(createNode(doc,Integer.toString(i),valueList.get(i)));
			}
		}
		//最后一概当成pojo类处理
		else{
			node=doc.createElement("bean");
			node.setAttribute("key", key);
			node.setAttribute("class", value.getClass().getName());
			
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
						node.appendChild(createNode(doc,props[i].getName(),reader.invoke(value, null)));
					}
				}
			}
		}
		
		return node;
	}
}
