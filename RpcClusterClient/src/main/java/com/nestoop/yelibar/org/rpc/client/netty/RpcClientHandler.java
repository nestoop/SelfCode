package com.nestoop.yelibar.org.rpc.client.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nestoop.yelibar.org.rpc.client.netty.decodeorcode.RpcClusterDecoder;
import com.nestoop.yelibar.org.rpc.client.netty.decodeorcode.RpcClusterEnCoder;
import com.nestoop.yelibar.org.rpc.client.request.RpcClusterRequest;
import com.nestoop.yelibar.org.rpc.client.response.RpcClusterResponse;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * netty 消息队列返回handler
 * 
 * @author xbao
 * @param <RpcClusterResponse>
 *
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcClusterResponse> {

	private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);
	//ip和端口
	private String host;
	private int port;
	//netty 响应实体
	private RpcClusterResponse response;

	private final Object obj = new Object();

	public RpcClientHandler(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	//放消息
	public RpcClusterResponse sendMessageToServer(RpcClusterRequest request){
		logger.debug("Rpc Client 向Netty的消息队列发送消息........");
		EventLoopGroup workgroup = new NioEventLoopGroup();
		
		try{
			 Bootstrap bootstrap = new Bootstrap();
			 //绑定Group
			 bootstrap.group(workgroup);
			 //客户端使用NioSocketChannel
			 bootstrap.channel(NioSocketChannel.class);
			 //加载消息处理流水
			 bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new RpcClusterEnCoder(RpcClusterRequest.class));
					ch.pipeline().addLast(new RpcClusterDecoder(RpcClusterResponse.class));
					ch.pipeline().addLast(RpcClientHandler.this);
				}
			}).option(ChannelOption.SO_KEEPALIVE, true);
			 
			 //启动
			 ChannelFuture future = bootstrap.connect(host, port).sync();
			 
	         future.channel().writeAndFlush(request).sync();

	          synchronized (obj) {
	                obj.wait(); // 未收到响应，使线程等待
	          }

	          if (response != null) {
	                future.channel().closeFuture().sync();
	          }
	         return response;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			workgroup.shutdownGracefully();
		}
		
		return response;
		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext handlerContext,RpcClusterResponse response) throws Exception {
		logger.debug("Rpc Client Netty 通道数据读取.........");
		this.response=response;
		 // 收到响应，唤醒线程
		synchronized (obj) {
            obj.notifyAll();
        }

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext handlerContext, Throwable cause)throws Exception {
		logger.error("Rpc Client Netty 异常，{}........",cause);
	}
	
	

}
