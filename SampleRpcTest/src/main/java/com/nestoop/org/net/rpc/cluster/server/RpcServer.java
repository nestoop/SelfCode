package com.nestoop.org.net.rpc.cluster.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.jboss.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.nestoop.org.net.rpc.cluster.netty.channelinit.RpcClusterChannelInitializer;
import com.nestoop.org.net.rpc.cluster.registry.RpcServiceRegistry;
import com.nestoop.org.net.rpc.cluster.spring.RPCScaneServer;

public class RpcServer implements ApplicationContextAware, InitializingBean{
	private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
	 //注册地址
	private  String serverAddress;
	//注册服务
	private  RpcServiceRegistry serviceRegistry;
	// 存放接口名与服务对象之间的映射关系,这个可以使用内存数据库如Redis代替
	private static Map<String, Object> handlerMap = new HashMap<String, Object>(); 

	public RpcServer(String serverAddress, RpcServiceRegistry serviceRegistry) {
		this.serverAddress = serverAddress;
		this.serviceRegistry = serviceRegistry;
	}
	
	public String getServerAddress(){
		return serverAddress;
	}
	
	public RpcServer(){
		logger.debug("serverAddress=="+serverAddress);
	}
	
	

	//获取spring 所有的注解的Bean
	public void setApplicationContext(ApplicationContext context)throws BeansException {
		logger.debug("从spring自定义的扫描器中所有注解的Bean.....................");
		//自定义的扫描器
		Map<String,Object> serviceMap=context.getBeansWithAnnotation(RPCScaneServer.class);
		//serviceMap是否有存在
		if(MapUtils.isNotEmpty(serviceMap)){
			//拿出Map的对象
			for(Object serviceBean:serviceMap.values()){
				logger.info("从spring所有RPCScaneServer注解的Bean：{}",serviceBean);
				//获取从spring所有RPCScaneServer注解的Bean的Name
				 String interfaceName = serviceBean.getClass().getAnnotation(RPCScaneServer.class).value().getName();
				 //放入缓存中
	             handlerMap.put(interfaceName, serviceBean);
			}
			
		}
		
	}
	
	//Netty 通信实现，有：请求 响应
	public void afterPropertiesSet() throws Exception {
		logger.debug("请求放入Netty,启动Netty......");
		 EventLoopGroup parentGroup = new NioEventLoopGroup();
	     EventLoopGroup childGroup = new NioEventLoopGroup();
	     logger.debug("server serverAddress:{}", serverAddress);
	     try{
	    	 ServerBootstrap bootstrap = new ServerBootstrap();
	    	 bootstrap.group(parentGroup, childGroup);
	    	 bootstrap.channel(NioServerSocketChannel.class);
	    	 bootstrap.childHandler(new RpcClusterChannelInitializer(handlerMap));
	    	 bootstrap.option(ChannelOption.SO_BACKLOG, 128);
             bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
             
             String[] array = serverAddress.split(":");
             String host = array[0];
             int port = Integer.parseInt(array[1]);
             logger.debug("server serverAddress host:{}", host);
             logger.debug("server serverAddress port:{}", port);
             logger.debug("server serviceRegistry :{}", serviceRegistry);
             if(serviceRegistry!=null){
            	 System.out.println("注册服务...............................");
            	 serviceRegistry.register(serverAddress);
             }
             //netty启动
             ChannelFuture future = (ChannelFuture) bootstrap.bind(host, port).sync();
             System.out.println("已经启动Netty...................................");
             future.getChannel().close().sync();
	     }catch(Exception e){
	    	 
	     }finally{
	    	 parentGroup.shutdownGracefully().sync();
	    	 childGroup.shutdownGracefully().sync();
	     }
		
	}
	

}
