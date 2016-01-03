package com.nestoop.yelibar.akka.zeromq;

import java.awt.Frame;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ExtendedActorSystem;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.event.slf4j.Logger;
import akka.serialization.Serialization;
import akka.serialization.SerializationExtension;
import akka.zeromq.Bind;
import akka.zeromq.ZMQMessage;
import akka.zeromq.ZeroMQExtension;

/**
 * ZeroMQ
 * @author akka
 *
 */
public class SampleZeroMQ {
	
	public static final String TICK = "TICK";
	
	public  static class Heap implements Serializable{
		
		private static final long serialVersionUID = 1L;
		
		public final long timestamp;
		public final long used;
		public final long max;
		
		public Heap(long timestamp, long used, long max) {
			this.timestamp = timestamp;
			this.used = used;
			this.max = max;
		}
				
	} 
	
	public static class Load implements Serializable{
		
		private static final long serialVersionUID = 1L;

		public  final long timestamp;
		
		public final double loadAverage;

		public Load(long timestamp, double loadAverage) {
			this.timestamp = timestamp;
			this.loadAverage = loadAverage;
		}
		
		
	}
	
	//创建一个健康监测器 actor
	
	public static class HealthProbe extends UntypedActor{
		
		final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
		
		//创建一个订阅者的socket,绑定ip和端口号
		
		ActorRef pubSocket=ZeroMQExtension.get(getContext().system().create("pubsockersystem")).newPubSocket(new Bind("tcp://localhost:9006"));
		//查看内存  使用jmx
		//内存
		MemoryMXBean memory=ManagementFactory.getMemoryMXBean();
		//操作系统
		OperatingSystemMXBean os=ManagementFactory.getOperatingSystemMXBean();
		//格式化
		Serialization sz=SerializationExtension.createExtension((ExtendedActorSystem) getContext().system());
		
		@Override
		public void onReceive(Object message) throws Exception {
			logger.debug("接收的消息是：",message);
			
			if(message instanceof String){
				//内存使用
				MemoryUsage currentHeap=memory.getHeapMemoryUsage();
				//当前时间
				long timestamp=System.currentTimeMillis();
				//格式化
				byte[] heapPayload =sz.serializerFor(Heap.class).toBinary(new Heap(timestamp, currentHeap.getUsed(), currentHeap.getMax()));
				
//				pubSocket.tell(new ZMQMessage(), getSender());
			}
		}

		@Override
		public void postRestart(Throwable reason) throws Exception {
			
		}

		@Override
		public void preStart() throws Exception {
			logger.debug("开始启动..........................");
			//从1秒后，每隔1秒发送一次消息为TICK
			getContext().system().scheduler().schedule(Duration.apply(1, TimeUnit.SECONDS), Duration.apply(1, TimeUnit.SECONDS), getSelf(),TICK, getContext().system().dispatcher(), null);
		}
		
		
		
		
	}

}
