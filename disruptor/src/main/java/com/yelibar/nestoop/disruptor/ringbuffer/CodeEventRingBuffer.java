package com.yelibar.nestoop.disruptor.ringbuffer;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.yelibar.nestoop.disruptor.event.CodeEvent;
import com.yelibar.nestoop.disruptor.event.handler.CodeEventHandler;

public class CodeEventRingBuffer {
	
	public static final int BUFFER_SIZE=1024;
	
	public static final int THREAD_NUM=10;
	//创建线程池
	public static ExecutorService executors = Executors.newFixedThreadPool(THREAD_NUM);  
	
	public static RingBuffer<CodeEvent> newInstanceSingleProducer(){
		
		return RingBuffer.createSingleProducer(CodeEvent.EVENT_FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
		
	}
	 //创建SequenceBarrier  
	public static SequenceBarrier loadSequenceBarrier(RingBuffer<CodeEvent> ringBuffer){
		return ringBuffer.newBarrier();
	}
	//创建批量时间处理器
	public static BatchEventProcessor<CodeEvent> loadBatchEventProcessor(RingBuffer<CodeEvent> ringBuffer,SequenceBarrier sequenceBarrier,CodeEventHandler eventHandler){
		
		return new BatchEventProcessor<CodeEvent>(ringBuffer, sequenceBarrier, eventHandler);
	} 
	//添加
	public static void addSequence(RingBuffer<CodeEvent> ringBuffer,BatchEventProcessor<CodeEvent> batchEventProcessor){
		ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
	}
	
	//提交给线程池
	public static void submitExecutors(BatchEventProcessor<CodeEvent> batchEventProcessor){
		executors.submit(batchEventProcessor);
	}
	
	public static void startDisputor(final RingBuffer<CodeEvent> ringBuffer){
		
		Future<CodeEvent> future=executors.submit(new Callable<CodeEvent>() {

			public CodeEvent call() throws Exception {
				CodeEvent event=null;
				 long seq;  
	                for(int i=0;i<1000;i++){  
	                    seq=ringBuffer.next();//占个坑 --ringBuffer一个可用区块  
	                      
	                    event=ringBuffer.get(seq);
	                    event.setCode(UUID.randomUUID().toString());;//给这个区块放入 数据  如果此处不理解，想想RingBuffer的结构图  
	                      
	                    ringBuffer.publish(seq);//发布这个区块的数据使handler(consumer)可见  
	                }
					return event;  
			}
		});
		
	}
	
	public static void main(String[] args) {
		startDisputor(newInstanceSingleProducer());
	}

}
