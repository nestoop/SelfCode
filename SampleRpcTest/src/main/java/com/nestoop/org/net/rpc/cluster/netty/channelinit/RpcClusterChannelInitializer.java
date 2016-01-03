package com.nestoop.org.net.rpc.cluster.netty.channelinit;

import java.util.HashMap;
import java.util.Map;

import com.nestoop.org.net.rpc.cluster.netty.decodeorcode.RpcClusterDecoder;
import com.nestoop.org.net.rpc.cluster.netty.handler.RpcClusterHandler;
import com.nestoop.org.net.rpc.cluster.request.RpcClusterRequest;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;



public class RpcClusterChannelInitializer extends ChannelInitializer<SocketChannel>{
	
	private Map<String, Object> handlerMap = new HashMap<String, Object>(); 

	public RpcClusterChannelInitializer(Map<String, Object> handlerMap) {
		this.handlerMap = handlerMap;
	}



	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		//解码
		ch.pipeline().addLast(new RpcClusterDecoder(RpcClusterRequest.class));
		ch.pipeline().addLast(new RpcClusterDecoder(RpcClusterRequest.class));
		ch.pipeline().addLast(new RpcClusterHandler(handlerMap));
		
	}

	
}
