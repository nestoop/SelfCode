package com.nestoop.org.net.rpc.cluster.util.serialize;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * 自定义格式化
 * @author xbao
 *
 */
public class RpcSerializationUtil {
	
	
	//protobuf 格式化
	public static class ProtoBufSerialiable{
		
		public static final Logger logger = LoggerFactory.getLogger(ProtoBufSerialiable.class);
		
		
		 private static Map<Class<?>, Schema<?>> cachedSchemaMap = new ConcurrentHashMap();

		 private static Objenesis objenesis = new ObjenesisStd(true);

		 private ProtoBufSerialiable() {}
		 
		 //获取schema
		 private static <T> Schema<T> getSchema(Class<T> classz){
			 
			 //缓存
			 @SuppressWarnings("unchecked")
			Schema<T> schema=(Schema<T>) cachedSchemaMap.get(classz);
			 //为空时，需要创建schema
			 if(schema == null ){
				//创建schema
				 schema=RuntimeSchema.createFrom(classz);
				 if(schema !=null){
					 cachedSchemaMap.put(classz, schema);
				 }
			 }
			 
			return schema;
			 
		 }
		 
		 //获取序列化的数据
		 
		 public static <T> byte[] serialize(T serializeObjcet){
			 
			 Class<T> classz=(Class<T>) serializeObjcet.getClass();
			 
			 //设置缓冲区，使用512，还有个256大小的缓冲区
			 LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
			 try{
				 //获取Schema
				 Schema<T> schema =getSchema(classz);
				 
				 return ProtostuffIOUtil.toByteArray(serializeObjcet, schema, buffer);
				 
			 }catch(Exception e){
				 logger.debug("protobuf 序列化出现异常...................");
				 e.printStackTrace();
			 }finally{
				 //清除缓冲区
				 buffer.clear();
			 }
			 
			 return null;
		 }
		 
		 //反序列化
		 
		 public static <T> T deSerialize(byte[] bytes,Class<T> classz){
			 
			 try{
				 T serializeObject=(T) objenesis.newInstance(classz);
				 Schema<T> schema=getSchema(classz);
				 ProtostuffIOUtil.mergeFrom(bytes, serializeObject, schema);
				 return serializeObject;
			 }catch(Exception e){
				 logger.debug("protobuf 反序列化出现异常...................");
				 e.printStackTrace();
			 }finally{
				 
			 }
			 
			 return null;
			 
		 }
		 
		
	}

}
