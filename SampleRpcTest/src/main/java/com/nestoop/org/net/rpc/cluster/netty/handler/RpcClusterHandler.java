package com.nestoop.org.net.rpc.cluster.netty.handler;

import java.util.Map;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nestoop.org.net.rpc.cluster.request.RpcClusterRequest;
import com.nestoop.org.net.rpc.cluster.response.RpcClusterResponse;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Netty的handler
 * @author xbao
 *
 */
public class RpcClusterHandler extends SimpleChannelInboundHandler<RpcClusterRequest>{
	
	private static final Logger logger = LoggerFactory.getLogger(RpcClusterHandler.class);

    private final Map<String, Object> handlerMap;

    public RpcClusterHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

	@Override
	protected void channelRead0(ChannelHandlerContext handlerContext,RpcClusterRequest request) throws Exception {
		//定义返回响应
		RpcClusterResponse response=new RpcClusterResponse();
		//设置请求id
		response.setRequestId(request.getRequestId());
		
		try {
			Object result=handlerRequestToObjct(request);
			
			response.setResult(result);
		} catch (Exception e) {
			response.setError(e);
		}
		
		handlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		
	}
	
	@SuppressWarnings("unused")
	private Object handlerRequestToObjct(RpcClusterRequest request) throws Exception {
		//请求的className
		String className=request.getClassName();
		logger.debug(String.format("请求的classname:%s", className));
		//从map获取接口的service
		Object serviceBean=handlerMap.get(className);
		logger.debug("请求的serviceBean:{}", serviceBean);
		//获取请求的service class
		Class<?> requestServiceClass=serviceBean.getClass();
		//请求的方法
		String requestServiceMethodName=request.getMethodName();
		logger.debug(String.format("请求的requestServiceMethodName:%s", requestServiceMethodName));
		//请求参数和请求参数类型
		Object[] requestParameters=request.getParameters();
		Class<?>[] requestParametersType=request.getParameterTypes();
		/**
		 * java 的反射机制
		 * 1：大众的反射机制
		 * 2：fastclass反射机制
		
		try {
			Method method=requestServiceClass.getMethod(requestServiceMethodName, requestParametersType);
			method.setAccessible(true);
			Object serviceObjcet= method.invoke(serviceBean, requestParameters);
			return serviceObjcet;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		 */
		//fastclass
		
		FastClass serviceFastClass=FastClass.create(requestServiceClass);
		FastMethod fastMethod=serviceFastClass.getMethod(requestServiceMethodName, requestParametersType);
		return  fastMethod.invoke(serviceBean, requestParameters);
		
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext handlerContext, Throwable cause) {
        logger.error("server caught exception", cause);
        handlerContext.close();
    }



}
