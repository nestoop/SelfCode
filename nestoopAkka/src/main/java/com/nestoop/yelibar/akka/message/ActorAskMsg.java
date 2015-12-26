package com.nestoop.yelibar.akka.message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.util.Timeout;

/**
 * 
 * ask方式发送消息
 * @author xbao
 *
 */
public class ActorAskMsg {
	
	
	
	public static void main(String[] args) {
		final Timeout t = new Timeout(Duration.create(5, TimeUnit.SECONDS));
		
		final ActorSystem system=ActorSystem.create("system");
		
		final ActorRef actorA=system.actorOf(Props.create(ActorMsgA.class),"ActorMsgA");
		final ActorRef actorB=system.actorOf(Props.create(ActorMsgB.class),"ActorMsgB");
		final ActorRef actorC=system.actorOf(Props.create(ActorMsgC.class),"ActorMsgC");
		final List<Future<Object>> ListFutures=new ArrayList<Future<Object>>();
		ListFutures.add(ask(actorA, "request", t));
		ListFutures.add(ask(actorB, "request", t));
		
		final Future<Iterable<Object>> aggregate = Futures.sequence(ListFutures, system.dispatcher());
		
	}
	
	private static Future<Object> ask(ActorRef actorA, String string, Timeout t) {
		return null;
	}

	/**
	 * Actor A
	 * @author 
	 *
	 */
	public static class ActorMsgA extends UntypedActor{

		@Override
		public void onReceive(Object message) throws Exception {
			
		}
		
	}
	/**
	 * ActorB
	 * @author 
	 *
	 */
	public static class ActorMsgB extends UntypedActor{
		
		@Override
		public void onReceive(Object message) throws Exception {
			
		}
		
	}
	/**
	 * ActorC
	 * @author 
	 *
	 */
	public static class ActorMsgC extends UntypedActor{
		
		@Override
		public void onReceive(Object message) throws Exception {
			
		}
		
	}
	

}
