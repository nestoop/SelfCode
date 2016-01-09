package com.liybar.nestoop.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

	/**
	 * �����channel
	 * @author Administrator
	 *
	 */
public   class DiscardServerChannleHandler extends ChannelInboundHandlerAdapter {
		/**
		 * ����ͨ����ȡ
		 */
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			
			System.out.println(String.format("server channel receive message {}",msg));
			 ByteBuf in = (ByteBuf) msg;
			try{
				//�Լ�������Ϣ����
				while (in.isReadable()) { // (1)
		            System.out.print(io.netty.util.CharsetUtil.US_ASCII);
		            System.out.flush();
		        }
			}catch(Throwable e){
				exceptionCaught(ctx,e);
			}finally{
				//�����������ݣ������kafka��Ϣ���б������죬�в��
				ReferenceCountUtil.release(msg);
			}
			
		}
		/**
		 * ������쳣ͨ���ر�
		 */
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
			
		}
		
		
		
	}