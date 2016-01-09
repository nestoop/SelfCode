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
			//�������¼���
			//���ڽ��ս���������
			EventLoopGroup parentGroup = new NioEventLoopGroup(); 
			//�����Ѿ������ܽ���������
	        EventLoopGroup childGroup = new NioEventLoopGroup();
	        
	        try{
		        //����NIO�ĸ���������
		        ServerBootstrap sbt=new ServerBootstrap();
		        
		        sbt.group(parentGroup, childGroup)
		        .channel(NioServerSocketChannel.class)
		        .childHandler(new DisCardChannelInitializer())
		        .option(ChannelOption.SO_BACKLOG, 128) //�����ṩ��NioServerSocketChannel���ս���������
		        .childOption(ChannelOption.SO_KEEPALIVE, true);//�����ṩ���ܵ�ServerChannel���ս��������ӣ������ǽ���NioServerSocketChannel
		        
		        //�󶨶˿ڣ���ʼ�����˿ڽ���������
		        ChannelFuture cf=sbt.bind(port).sync();
		        //�رշ���
		        cf.channel().closeFuture().sync();
	        }finally{
	        	parentGroup.shutdownGracefully();
	        	childGroup.shutdownGracefully();
	        	
	        }
		}
	}
	
	
}
