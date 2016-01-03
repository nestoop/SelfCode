package com.nestoop.yelibar.org.rpc.client.netty.decodeorcode;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nestoop.yelibar.org.rpc.client.serialize.RpcSerializationUtil.ProtoBufSerialiable;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 实现Netty的解码
 * @author xbao
 *
 */
public class RpcClusterDecoder extends ByteToMessageDecoder {
	
	public static final Logger logger = LoggerFactory.getLogger(ByteToMessageDecoder.class);
	
	private Class<?> classz;

	public RpcClusterDecoder(Class<?> classz) {
		super();
		this.classz = classz;
	}


	@Override
	protected void decode(ChannelHandlerContext handlercontext, ByteBuf in,List<Object> out) throws Exception {
		//判断读取的字节小于4 返回
		if(in.readableBytes()<4){
			logger.debug("buf中读取的长度,buf.readableBytes={}",in.readableBytes());
			return;
		}
		//继续读取
		in.markReaderIndex();
		//获取数据的长度
		int targetDataLength=in.readInt();
		//没有内容读取
		if(targetDataLength<0){
			handlercontext.close();
		}
		
		//如果已经可读到的数据长度大于数据长度,需要重新读取
		if(in.readableBytes()<targetDataLength){
			in.resetReaderIndex();
		}
		
		byte[] data=new byte[targetDataLength];
		
		in.readBytes(data);
		
		Object object=ProtoBufSerialiable.deSerialize(data, classz);
		
		out.add(object);
		
		
	}
	
	

}
