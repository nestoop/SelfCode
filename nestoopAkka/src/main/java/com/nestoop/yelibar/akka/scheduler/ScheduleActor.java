package com.nestoop.yelibar.akka.scheduler;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ScheduleActor {
	
	public static void main(String[] args) {
		
		final ActorSystem system=ActorSystem.create("system");
		
		final ActorRef testscheduleActor=system.actorOf(Props.create(TestScheduleActor.class),"testschedule");
		//��testscheduleActor ��50ms������Ϣ
		system.scheduler().scheduleOnce(Duration.create(50,TimeUnit.MILLISECONDS), testscheduleActor, "foo", system.dispatcher(), null);
		//����һ���߳�,���͵�ǰ�¼�����50ms��ִ��
		system.scheduler().scheduleOnce(Duration.create(50,TimeUnit.MILLISECONDS),new Runnable() {
			
			@Override
			public void run() {
				testscheduleActor.tell(System.currentTimeMillis(), ActorRef.noSender());
				
			}
		}, system.dispatcher());
		
		//ȡ������
		
		final ActorRef tickerActor=system.actorOf(Props.create(TickerActor.class),"TickerActor");
		//��0m֮��ÿ��50ms ��tickerActor������Ϣ
		Cancellable cancellable=system.scheduler().schedule(Duration.Zero(), Duration.create(50,TimeUnit.MILLISECONDS), tickerActor, "Ticker", system.dispatcher(), null);
		//ȡ������
		cancellable.cancel();
		
	}
	
	
	//����Actor
	public 	static class TestScheduleActor extends UntypedActor{

		@Override
		public void onReceive(Object message) throws Exception {
			System.out.println("message=="+message);
			
			if(message instanceof String){
				System.out.println("�����������ַ�����message=="+message);
			}else if(message instanceof Long){
				System.out.println("�������ĵ�ǰʱ�䣬message=="+message);
			}else{
				unhandled(message);
			}
			
			
		}
		
	}
	//ȡ������
	public static class TickerActor extends UntypedActor{

		@Override
		public void onReceive(Object message) throws Exception {
			
			if(message.equals("Ticker")){
				System.out.println("ȡ�����ȣ�message=="+message);
				
			}else{
				unhandled(message);
			}
		}
		
		
	}
	
	
	

}
