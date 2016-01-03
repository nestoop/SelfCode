package com.nestoop.yelibar.akka.fsm;

import java.util.List;

import akka.actor.ActorRef;

public class FSMMessageBuncher {
	
	public final Object flush =new Object();
	
	
	public static class SetTarget{
		
		final ActorRef ref;
		
		public SetTarget(ActorRef ref){
			this.ref=ref;
		}
	}
	
	public static class Queue{
		
		final Object obj;
		
		public Queue(Object obj){
			this.obj=obj;
		}
	}
	
	public static class Batch{
		
		final List<Object> objects;
		
		public Batch(List<Object> objects){
			this.objects=objects;
		}
	}
	
	

}
