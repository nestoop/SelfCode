package com.liybar.nestoop.netty.test;

import com.liybar.nestoop.netty.handler.DiscardServerChannleHandler;
import com.liybar.nestoop.netty.handler.TimerChannelHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class DisCardChannelInitializer extends ChannelInitializer<SocketChannel> {
	/**
	 * 通常处理最后接收的数据
	 */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
		ch.pipeline().addFirst(new TimerChannelHandler());
		//最后 接收的是这个handler
		ch.pipeline().addLast(new DiscardServerChannleHandler());
		
		
	}


	

	

}
