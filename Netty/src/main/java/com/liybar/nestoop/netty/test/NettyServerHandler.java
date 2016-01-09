package com.liybar.nestoop.netty.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;

public class NettyServerHandler {
	
	public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new DisCardServer(port).run();
    }
	
	
	
	
	/**
	 * Discard Server
	 */
	
	public static class DisCardServer {
		
		private int port;
		
		public DisCardServer(int  port){
			this.port=port;
		}
		
		
		public void run() throws Exception{
			//定义轮事件组
			//用于接收进来的链接
			EventLoopGroup parentGroup = new NioEventLoopGroup(); 
			//处理已经被接受进来的链接
	        EventLoopGroup childGroup = new NioEventLoopGroup();
	        
	        try{
		        //启动NIO的辅助启动类
		        ServerBootstrap sbt=new ServerBootstrap();
		        
		        sbt.group(parentGroup, childGroup)
		        .channel(NioServerSocketChannel.class)
		        .childHandler(new DisCardChannelInitializer())
		        .option(ChannelOption.SO_BACKLOG, 128) //用于提供给NioServerSocketChannel接收进来的链接
		        .childOption(ChannelOption.SO_KEEPALIVE, true);//用于提供父管道ServerChannel接收进来的链接，本例是接收NioServerSocketChannel
		        
		        //绑定端口，开始监听端口进来的数据
		        ChannelFuture cf=sbt.bind(port).sync();
		        //关闭服务
		        cf.channel().closeFuture().sync();
	        }finally{
	        	parentGroup.shutdownGracefully();
	        	childGroup.shutdownGracefully();
	        	
	        }
		}
	}
	
	
}
