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
		//向testscheduleActor 在50ms后发送消息
		system.scheduler().scheduleOnce(Duration.create(50,TimeUnit.MILLISECONDS), testscheduleActor, "foo", system.dispatcher(), null);
		//调度一个线程,发送当前事件，在50ms后执行
		system.scheduler().scheduleOnce(Duration.create(50,TimeUnit.MILLISECONDS),new Runnable() {
			
			@Override
			public void run() {
				testscheduleActor.tell(System.currentTimeMillis(), ActorRef.noSender());
				
			}
		}, system.dispatcher());
		
		//取消调度
		
		final ActorRef tickerActor=system.actorOf(Props.create(TickerActor.class),"TickerActor");
		//在0m之后，每隔50ms 向tickerActor发送消息
		Cancellable cancellable=system.scheduler().schedule(Duration.Zero(), Duration.create(50,TimeUnit.MILLISECONDS), tickerActor, "Ticker", system.dispatcher(), null);
		//取消调度
		cancellable.cancel();
		
	}
	
	
	//定义Actor
	public 	static class TestScheduleActor extends UntypedActor{

		@Override
		public void onReceive(Object message) throws Exception {
			System.out.println("message=="+message);
			
			if(message instanceof String){
				System.out.println("发过来的是字符串，message=="+message);
			}else if(message instanceof Long){
				System.out.println("发过来的当前时间，message=="+message);
			}else{
				unhandled(message);
			}
			
			
		}
		
	}
	//取消调度
	public static class TickerActor extends UntypedActor{

		@Override
		public void onReceive(Object message) throws Exception {
			
			if(message.equals("Ticker")){
				System.out.println("取消调度，message=="+message);
				
			}else{
				unhandled(message);
			}
		}
		
		
	}
	
	
	

}
