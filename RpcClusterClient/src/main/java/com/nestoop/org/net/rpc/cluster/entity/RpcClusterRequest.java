package com.nestoop.org.net.rpc.cluster.entity;

import java.io.Serializable;

/**
 * 请求
 * @author xbao
 *
 */
public class RpcClusterRequest implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	//请求ID
	private String requestId;
	//请求classname
    private String className;
    //请求的方法
    private String methodName;
    //参数类型
    private Class<?>[] parameterTypes;
    //参数
    private Object[] parameters;
    
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

}
