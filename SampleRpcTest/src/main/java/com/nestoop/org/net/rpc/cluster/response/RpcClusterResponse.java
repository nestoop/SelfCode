package com.nestoop.org.net.rpc.cluster.response;

/**
 * 请求响应
 * @author xbao
 *
 */
public class RpcClusterResponse {
	//请求的Id
	private String requestId;
	//响应异常信息
    private Throwable error;
    //返回的结果
    private Object result;
    
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Throwable getError() {
		return error;
	}
	public void setError(Throwable error) {
		this.error = error;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
    
    

}
