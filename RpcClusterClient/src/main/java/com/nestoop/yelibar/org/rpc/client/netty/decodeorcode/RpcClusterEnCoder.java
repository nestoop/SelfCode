package com.nestoop.yelibar.org.rpc.client.netty.decodeorcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.nestoop.org.net.rpc.cluster.entity.RpcClusterRequest;
import com.nestoop.yelibar.org.rpc.client.serialize.RpcSerializationUtil.ProtoBufSerialiable;

/**
 * 使用Netty编码功能 集成MessageToByteEncoder
 * @author xbao
 *
 */
public class RpcClusterEnCoder extends MessageToByteEncoder<RpcClusterRequest>{
	
	public static final Logger logger = LoggerFactory.getLogger(RpcClusterEnCoder.class);
	
	private Class<?> classz;
	

	public RpcClusterEnCoder(Class<?> classz) {
		this.classz = classz;
	}

	@Override
	protected void encode(ChannelHandlerContext handlerContext, RpcClusterRequest inobject, ByteBuf outBuf)throws Exception {
		logger.debug("客户端请求编码对象：{}",inobject);
		if(classz.isInstance(inobject)){
			byte[] dataByteArray=ProtoBufSerialiable.serialize(inobject);
			outBuf.writeInt(dataByteArray.length);
			outBuf.writeBytes(dataByteArray);
		}
		
	}

}
