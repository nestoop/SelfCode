package com.nestoop.yelibar.org.rpc.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nestoop.yelibar.org.rpc.client.netty.RpcClientHandler;
import com.nestoop.yelibar.org.rpc.client.registry.RpcClientServiceRegistry;
import com.nestoop.yelibar.org.rpc.client.request.RpcClusterRequest;
import com.nestoop.yelibar.org.rpc.client.response.RpcClusterResponse;
/**
 * 客户端代理
 * @author xbao
 *
 */
public class RpcProxy {
	
	private RpcClientServiceRegistry clientRegistry;
	
	private String registryAddress;

	public RpcProxy(RpcClientServiceRegistry clientRegistry) {
		this.clientRegistry = clientRegistry;
	}

	public RpcProxy(String registryAddress) {
		this.registryAddress = registryAddress;
	}
	
	

	public RpcProxy(RpcClientServiceRegistry clientRegistry,
			String registryAddress) {
		this.clientRegistry = clientRegistry;
		this.registryAddress = registryAddress;
	}

	public RpcProxy() {}
	
	
	/**
	 * 使用动态了道理模式创建实体
	 */
	
	@SuppressWarnings("unchecked")
	public <T> T createRpcService(Class<?> interfaceClass){
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[]{interfaceClass}, new RpcProxyInvocationHandler(registryAddress,clientRegistry));
	}
	
	public static class RpcProxyInvocationHandler implements InvocationHandler{
		
		private static final Logger logger = LoggerFactory.getLogger(RpcProxyInvocationHandler.class);
		
		private RpcClientServiceRegistry clientRegistry;
		
		private String registryAddress;
		
		public RpcProxyInvocationHandler(String registryAddress,RpcClientServiceRegistry clientRegistry){
			this.registryAddress=registryAddress;
			this.clientRegistry=clientRegistry;
		}

		public Object invoke(Object proxy, Method method, Object[] parameters) throws Throwable {
			//创建一个请求
			RpcClusterRequest request=new RpcClusterRequest();
			
			request.setClassName(method.getDeclaringClass().getName());
			request.setMethodName(method.getName());
			request.setParameters(parameters);
			request.setParameterTypes(method.getParameterTypes());
			request.setRequestId(UUID.randomUUID().toString());
			
			//寻找zookeeper
			if(clientRegistry != null){
				clientRegistry.discover();
				
			}
			//分割zookeeper地址，找到zookeeper
			String[] array = registryAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            //创建rpc client;
            RpcClientHandler rpcClienthandler=new RpcClientHandler(host, port);
            //发送消息
            RpcClusterResponse response = rpcClienthandler.sendMessageToServer(request);
			
            if(response.getError() !=null){
            	logger.error("Rpc Client Proxy 动态代理出现错误 ,错误信息:{}", response.getError());
            	throw response.getError();
            }else{
            	logger.error("Rpc Client Proxy i非常正确 ,正确返回对象是:{}", response.getResult());
            	return response.getResult();
            }
		}
		
		
	}

}
