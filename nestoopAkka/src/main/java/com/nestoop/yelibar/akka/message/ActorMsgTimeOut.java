package com.nestoop.yelibar.akka.message;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ActorMsgTimeOut extends UntypedActor{

	@Override
	public void onReceive(Object message) throws Exception {
		System.out.println("message=="+message);
		if (message.equals("hello")) {
			getSender().tell("Hello world",getSelf());
		} else if (message == getContext().receiveTimeout()) {
			System.out.println("时间操作10s后的actor处理");
			throw new RuntimeException("received timeout");
		} else {
			unhandled(message);
		}

	}
	
	public ActorMsgTimeOut(){
		 getContext().setReceiveTimeout(Duration.apply("10 seconds"));
	}
	
	public static void main(String[] args) {
		
		ActorSystem system=ActorSystem.create("system");
		
		ActorRef child =system.actorOf(Props.create(ActorMsgTimeOut.class),"child");
		
		child.tell("hello", child);
		child.tell(PoisonPill.getInstance(),child);
		
		
	}
	
	

}
