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
	
	//����һ����������� actor
	
	public static class HealthProbe extends UntypedActor{
		
		final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
		
		//����һ�������ߵ�socket,��ip�Ͷ˿ں�
		
		ActorRef pubSocket=ZeroMQExtension.get(getContext().system().create("pubsockersystem")).newPubSocket(new Bind("tcp://localhost:9006"));
		//�鿴�ڴ�  ʹ��jmx
		//�ڴ�
		MemoryMXBean memory=ManagementFactory.getMemoryMXBean();
		//����ϵͳ
		OperatingSystemMXBean os=ManagementFactory.getOperatingSystemMXBean();
		//��ʽ��
		Serialization sz=SerializationExtension.createExtension((ExtendedActorSystem) getContext().system());
		
		@Override
		public void onReceive(Object message) throws Exception {
			logger.debug("���յ���Ϣ�ǣ�",message);
			
			if(message instanceof String){
				//�ڴ�ʹ��
				MemoryUsage currentHeap=memory.getHeapMemoryUsage();
				//��ǰʱ��
				long timestamp=System.currentTimeMillis();
				//��ʽ��
				byte[] heapPayload =sz.serializerFor(Heap.class).toBinary(new Heap(timestamp, currentHeap.getUsed(), currentHeap.getMax()));
				
//				pubSocket.tell(new ZMQMessage(), getSender());
			}
		}

		@Override
		public void postRestart(Throwable reason) throws Exception {
			
		}

		@Override
		public void preStart() throws Exception {
			logger.debug("��ʼ����..........................");
			//��1���ÿ��1�뷢��һ����ϢΪTICK
			getContext().system().scheduler().schedule(Duration.apply(1, TimeUnit.SECONDS), Duration.apply(1, TimeUnit.SECONDS), getSelf(),TICK, getContext().system().dispatcher(), null);
		}
		
		
		
		
	}

}
