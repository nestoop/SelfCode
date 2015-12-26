package com.nestoop.yelibar.akka.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * akka的消息
 * @author xbao
 *
 */
public class ActorImmutableMessage  {
	
	public static final ActorSystem system=ActorSystem.create("system");
	
	
	private final int squenceNumber;
	
	private final List<String> values;

	public ActorImmutableMessage(int squenceNumber, List<String> values) {
		this.squenceNumber = squenceNumber;
		this.values = Collections.unmodifiableList(new  ArrayList<String>(values));
	}

	public int getSquenceNumber() {
		return squenceNumber;
	}


	public List<String> getValues() {
		return values;
	}
	
	public static class MsgActor extends UntypedActor{
		ActorRef child=system.actorOf(Props.create(MsgActor.class),"MsgActor");
		

		public void onReceive(Object message) throws Exception {
			/*1:getSelf是ActorRef的一个变量
			 *2:第二个参数是getSelf().tell();
			 *3:也就是getSelf()返回响应
			 */
			child.tell("hello",getSelf());
			
			
		}
		
	}

}
