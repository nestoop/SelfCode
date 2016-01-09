package com.liybar.nestoop.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

	/**
	 * 服务端channel
	 * @author Administrator
	 *
	 */
public   class DiscardServerChannleHandler extends ChannelInboundHandlerAdapter {
		/**
		 * 服务通道读取
		 */
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			
			System.out.println(String.format("server channel receive message {}",msg));
			 ByteBuf in = (ByteBuf) msg;
			try{
				//自己处理消息队列
				while (in.isReadable()) { // (1)
		            System.out.print(io.netty.util.CharsetUtil.US_ASCII);
		            System.out.flush();
		        }
			}catch(Throwable e){
				exceptionCaught(ctx,e);
			}finally{
				//最终抛弃数据，这个和kafka消息队列保存七天，有差别
				ReferenceCountUtil.release(msg);
			}
			
		}
		/**
		 * 服务端异常通道关闭
		 */
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			cause.printStackTrace();
			ctx.close();
			
		}
		
		
		
	}