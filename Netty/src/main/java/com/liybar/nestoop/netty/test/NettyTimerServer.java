package com.liybar.nestoop.netty.test;

import java.util.Date;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.ChannelInitializer;

public class NettyTimerServer {
	
	public static void main(String[] args) throws Exception {

        String host = "192.168.0.100";
        int port = Integer.parseInt("8085");
        //既是boss group  也是work group
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // 表示非服务端或者无连接模式的channel
            b.group(workerGroup); // 既是work group 也是boss group
            b.channel(NioSocketChannel.class); // 客户端的chanel
            b.option(ChannelOption.SO_KEEPALIVE, true); // 没有child channel 没有child option
            b.handler(new ChannelInitializer<SocketChannel>() { //处理最后接收的消息
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });

            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // 等待连接关闭
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
	
	public static  class TimeClientHandler extends  ChannelInboundHandlerAdapter{
		private  ByteBuf buf;
		 @Override
		    public void channelRead(ChannelHandlerContext ctx, Object msg) {
			 ByteBuf m = (ByteBuf) msg; // (1)
			 //写入缓冲区
			 buf.writeBytes(m);
			 //丢弃消息
			 m.release();
			 //读取缓冲区的字节大小，大于我定义的字节大小size,表示可以处理自己的业务逻辑，否则一直执行此方法，知道满足条件
		     if (buf.readableBytes() >= 4) { // (3)
		         long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
		         System.out.println(new Date(currentTimeMillis));
		         ctx.close();
		     }
		        	
		    }

		    @Override
		    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		        cause.printStackTrace();
		        ctx.close();
		    }
		    
			@Override
			public void handlerAdded(ChannelHandlerContext ctx)
					throws Exception {
				//定义buf 的大小
				ctx.alloc().buffer(4);
			}

			@Override
			public void handlerRemoved(ChannelHandlerContext ctx)
					throws Exception {
				//handler释放缓冲区
				buf.release();
				
				buf=null;
				
			}
		    
	}

}
