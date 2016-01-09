package com.nestoop.org.net.rpc.cluster.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
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

import com.nestoop.org.net.rpc.cluster.netty.decodeorcode.RpcClusterDecoder;
import com.nestoop.org.net.rpc.cluster.netty.decodeorcode.RpcClusterEnCoder;
import com.nestoop.org.net.rpc.cluster.netty.handler.RpcClusterHandler;
import com.nestoop.org.net.rpc.cluster.registry.RpcServiceRegistry;
import com.nestoop.org.net.rpc.cluster.request.RpcClusterRequest;
import com.nestoop.org.net.rpc.cluster.response.RpcClusterResponse;
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
		logger.debug("serverAddress=="+serverAddress);
	}
	
	public String getServerAddress(){
		return serverAddress;
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
	
	public void start() throws InterruptedException{
		
		ServerSocketChannel acceptorSvr=null;
		try {
			acceptorSvr = ServerSocketChannel.open();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		logger.debug("请求放入Netty,启动Netty......");
		 EventLoopGroup parentGroup = new NioEventLoopGroup();
	     EventLoopGroup childGroup = new NioEventLoopGroup();
	     logger.debug("Netty server serverAddress:{}", serverAddress);
	     try{
	    	 ServerBootstrap bootstrap = new ServerBootstrap();
	    	 bootstrap.group(parentGroup, childGroup);
	    	 bootstrap.channel(NioServerSocketChannel.class);
	    	 bootstrap.childHandler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new RpcClusterDecoder(RpcClusterRequest.class));
					ch.pipeline().addLast(new RpcClusterEnCoder(RpcClusterResponse.class));
					ch.pipeline().addLast(new RpcClusterHandler(handlerMap));
				}
	    		 
	    	 });
	    	bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            
            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            logger.debug("Netty server start host:{}", host);
            //netty启动
            ChannelFuture future = (ChannelFuture) bootstrap.bind(port).sync();
//            future.getChannel().close().sync();
	     }catch(Exception e){
	    	 
	     }finally{
//	    	 parentGroup.shutdownGracefully().sync();
//	    	 childGroup.shutdownGracefully().sync();
	     }
		
	}
	
	
	//Netty 通信实现，有：请求 响应
	public void afterPropertiesSet() throws Exception {
		
       if(serviceRegistry!=null){
         System.out.println("注册服务...............................");
          serviceRegistry.register(serverAddress);
        }
            
	}
	
	
	

}
