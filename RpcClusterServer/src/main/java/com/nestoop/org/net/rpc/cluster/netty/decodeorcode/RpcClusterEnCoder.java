package com.nestoop.org.net.rpc.cluster.netty.decodeorcode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import com.nestoop.org.net.rpc.cluster.util.serialize.RpcSerializationUtil.ProtoBufSerialiable;

/**
 * 使用Netty编码功能 集成MessageToByteEncoder
 * @author xbao
 *
 */
public class RpcClusterEnCoder extends MessageToByteEncoder{
	
	private Class<?> classz;
	

	public RpcClusterEnCoder(Class<?> classz) {
		this.classz = classz;
	}

	@Override
	protected void encode(ChannelHandlerContext handlerContext, Object inobject, ByteBuf outBuf)throws Exception {
		
		if(classz.isInstance(inobject)){
			byte[] dataByteArray=ProtoBufSerialiable.serialize(inobject);
			outBuf.writeInt(dataByteArray.length);
			outBuf.writeBytes(dataByteArray);
		}
		
	}

}
