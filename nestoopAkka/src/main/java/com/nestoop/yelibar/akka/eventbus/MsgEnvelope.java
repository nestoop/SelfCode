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
	 * ����MsgEnvelope��topic�Ľ��ж��ģ�����payload
	 * @author Administrator
	 *
	 */
	public static class LookupBusImpl extends LookupEventBus<MsgEnvelope,ActorRef,String>{
		/**
		 * ���¼����߻�ȡ������
		 */
		@Override
		public String classify(MsgEnvelope event) {
			System.out.println("��ȡʵ������=="+event.topic);
			return event.topic;
		}
		/**
		 * ʵ��ȫ����
		 */
		@Override
		public int compareSubscribers(ActorRef subscriber1, ActorRef subscriber2) {
			// TODO Auto-generated method stub
			return subscriber1.compareTo(subscriber2);
		}
		/**
		 * �����������ݽṹ�ĳ�ʼ��С
		 */
		@Override
		public int mapSize() {
			// TODO Auto-generated method stub
			return 128;
		}
		/**
		 * ע�ᵽ ��ǰ�¼��ķ����������ж����ߵ�ÿһ���¼����е���
		 */
		@Override
		public void publish(MsgEnvelope event, ActorRef subscriber) {
			System.out.println("�¼����������߷����ķ�������"+event.topic);
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
