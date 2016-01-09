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
        //����boss group  Ҳ��work group
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // ��ʾ�Ƿ���˻���������ģʽ��channel
            b.group(workerGroup); // ����work group Ҳ��boss group
            b.channel(NioSocketChannel.class); // �ͻ��˵�chanel
            b.option(ChannelOption.SO_KEEPALIVE, true); // û��child channel û��child option
            b.handler(new ChannelInitializer<SocketChannel>() { //���������յ���Ϣ
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });

            // �����ͻ���
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // �ȴ����ӹر�
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
			 //д�뻺����
			 buf.writeBytes(m);
			 //������Ϣ
			 m.release();
			 //��ȡ���������ֽڴ�С�������Ҷ�����ֽڴ�Сsize,��ʾ���Դ����Լ���ҵ���߼�������һֱִ�д˷�����֪����������
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
				//����buf �Ĵ�С
				ctx.alloc().buffer(4);
			}

			@Override
			public void handlerRemoved(ChannelHandlerContext ctx)
					throws Exception {
				//handler�ͷŻ�����
				buf.release();
				
				buf=null;
				
			}
		    
	}

}
