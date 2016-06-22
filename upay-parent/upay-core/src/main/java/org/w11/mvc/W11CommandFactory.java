package org.w11.mvc;

import org.w11.Command;

/**
 * command factory抽象类
 * 其子类可以支持普通的Command模式，或是分布式部署的Command模式
 * @author zhw
 */
public class W11CommandFactory {
	
    private static W11CommandFactory instance=new W11CommandFactory();
    public static W11CommandFactory getInstance(){
    	return instance;
    }

    /**
     * 获取省略的包前缀，末尾加'.'
     */
    protected String getDefaultPackagePrefix(){
    	String prefix = MvcSettings.getInstance().getDefaultPackagePrefix();
    	if(prefix==null||"".equals(prefix)){
    		return "";
    	}
    	return prefix.endsWith(".") ? prefix : prefix + '.';
    }
    
    /**
     * 获取不需要追加类前缀的路径
     */
    protected String[] getExludePackagePrefixArray(){
    	String excludePrefix=MvcSettings.getInstance().getExcludePackagePrefix();
    	return (excludePrefix==null) ? new String[0]:excludePrefix.split(",");
    }
    
    /**
     * 获取command完整路径
     */
    protected String getCommandClassPath(String commandId){
    	
    	//确定cmd完整路径
    	String defaultPackagePrefix=getDefaultPackagePrefix();
    	String[] exludePackagePrefix=getExludePackagePrefixArray();
    	boolean needPrefix=true;
    	for(int i=0;i<exludePackagePrefix.length;i++){
    		if(commandId.startsWith(exludePackagePrefix[i])){
    			needPrefix=false;
    			break;
    		}
    	}
    	if(needPrefix) return defaultPackagePrefix+commandId;
    	else return commandId;
    }
    
    /**
     * 创建command实例
     */
    public Command getCommand(String commandId) throws W11Exception {
        	String commandClassPath=getCommandClassPath(commandId);
        	try {
				return (Command)Class.forName(commandClassPath).newInstance();
			} catch (InstantiationException e) {
		       	throw new W11Exception("调用"+commandId+"时产生异常，无法调用构造函数", e);
			} catch (IllegalAccessException e) {
		       	throw new W11Exception("调用"+commandId+"时产生异常，没有相关类的访问权限", e);
			} catch (ClassNotFoundException e) {
		       	throw new W11Exception("调用"+commandId+"时产生异常，找不到相关类", e);
			}catch(ClassCastException e){
		       	throw new W11Exception("调用"+commandId+"时产生异常，相关类必须继承自"+Command.class.getName(), e);
			}
    	
    }
}
