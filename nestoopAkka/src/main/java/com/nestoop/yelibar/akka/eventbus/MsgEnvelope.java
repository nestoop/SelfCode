package com.nestoop.yelibar.akka.eventbus;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.japi.LookupEventBus;

public class MsgEnvelope{
	
	public final String topic;
	
	public final Object payload;
	
	static final ActorSystem system=ActorSystem.create("system");

	public MsgEnvelope(String topic, Object payload) {
		this.topic = topic;
		this.payload = payload;
	}
	/**
	 * 具有MsgEnvelope的topic的进行订阅，发布payload
	 * @author Administrator
	 *
	 */
	public static class LookupBusImpl extends LookupEventBus<MsgEnvelope,ActorRef,String>{
		/**
		 * 从事件总线获取分类器
		 */
		@Override
		public String classify(MsgEnvelope event) {
			System.out.println("获取实现总线=="+event.topic);
			return event.topic;
		}
		/**
		 * 实现全排序
		 */
		@Override
		public int compareSubscribers(ActorRef subscriber1, ActorRef subscriber2) {
			// TODO Auto-generated method stub
			return subscriber1.compareTo(subscriber2);
		}
		/**
		 * 决定这种数据结构的初始大小
		 */
		@Override
		public int mapSize() {
			// TODO Auto-generated method stub
			return 128;
		}
		/**
		 * 注册到 当前事件的分类器的所有订阅者的每一个事件进行调用
		 */
		@Override
		public void publish(MsgEnvelope event, ActorRef subscriber) {
			System.out.println("事件总线向订阅者发布的分类器："+event.topic);
			subscriber.tell(event.payload, ActorRef.noSender());
		}
		
		
	}
	
	public static class TestEventBusActor  extends UntypedActor{

		@Override
		public void onReceive(Object message) throws Exception {
			
			System.out.println("message=="+message);
		}
		
		
	}
	
	public static void main(String[] args) {
		
		LookupBusImpl lookupBus = new LookupBusImpl();
		
		lookupBus.subscribe(getTestActor(), "greetings");
		
		lookupBus.publish(new MsgEnvelope("time", System.currentTimeMillis()));
		lookupBus.publish(new MsgEnvelope("greetings", "hello"));
		
		expectMsgEquals("hello");
		
	}
	
	public static ActorRef getTestActor(){
		
		return system.actorOf(Props.create(TestEventBusActor.class),"TestEventBusActor");
	}
	
	
	public static void expectMsgEquals(String msg){
		
		
	}
	
	
	
}
