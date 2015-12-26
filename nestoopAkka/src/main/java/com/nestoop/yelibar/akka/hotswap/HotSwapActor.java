package com.nestoop.yelibar.akka.hotswap;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Procedure;

/**
 * »»≤Â∞Œactor
 * @author xbao
 *
 */
public class HotSwapActor extends UntypedActor{
	
	final int i=0;
	
	public static void main(String[] args) {
		ActorSystem system=ActorSystem.create("system");
		ActorRef swap=system.actorOf(Props.create(HotSwapActor.class),"hotswapactor");
		
		swap.tell("bar",swap);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if(message.equals("bar")){
			getContext().become(angery);			
		}else if(message.equals("foo")){
			getContext().become(happy);
		}else{
			unhandled(message);
		}
		
	}
	
	//angery procedure
	final Procedure<Object> angery=new Procedure<Object>() {
		
		@Override
		public void apply(Object message) throws Exception {
			System.out.println((i+1)+"----->"+(String)message);
			if(message.equals("foo")){
				getSender().tell("I am angery?",ActorRef.noSender());				
			}else if(message.equals("foo")){
				getContext().become(happy);				
			}
		}
	};
	//happy procedure
	final Procedure<Object> happy=new Procedure<Object>() {
		
		@Override
		public void apply(Object message) throws Exception {
			System.out.println((i+1)+"----->"+(String)message);
			if(message.equals("bar")){
				getSender().tell("I am happy -:)",ActorRef.noSender());				
			}else if(message.equals("foo")){
				getContext().become(angery);				
			}
		}
	};

}
