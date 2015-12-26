package com.nestoop.yelibar.akka.actor.dispatcher;

import com.nestoop.yelibar.akka.actor.dispatcher.DispatcherConfigExtension.ConfigSetting;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * 
 * 
 * @author AKKAµÄ·Ö·¢Æ÷
 *
 */
public class AKKADispatcher {
	
	
	public static void main(String[] args) {
		
		ActorSystem system=ActorSystem.create("system");
		
		ActorRef dispatcher=system.actorOf(Props.create(TestDispatcher.class).withDispatcher("default-dispatcher"), "dispatcher");
		
		dispatcher.tell("message", dispatcher);
		
		
	}
	
	
	public  static class TestDispatcher extends UntypedActor{
		
		final DispatcherConfigExtension dispatcherConfig=ConfigSetting.SettingsProvider.get(getContext().system());
		@Override
		public void onReceive(Object message) throws Exception {
			if(message instanceof String){
				System.out.println(message);
				System.out.println(dispatcherConfig.Load("default-dispatcher.type"));
				
			}
			
		}
		
		
	}

}
