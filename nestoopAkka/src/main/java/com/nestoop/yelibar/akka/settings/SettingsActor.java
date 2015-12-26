package com.nestoop.yelibar.akka.settings;


import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.Duration;

import com.nestoop.yelibar.akka.settings.SettingUtil.Settings;
import com.nestoop.yelibar.akka.settings.SettingUtil.SettingsImpl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

public class SettingsActor extends UntypedActor {
	
	 final SettingsImpl settings=Settings.SettingsProvider.get(getContext().system());
	

	 public SettingsActor(){}
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof String){
			System.out.println(message);
			System.out.println(settings.DB_URI);
			
		}
		
	}

	public static class PrarmCreator implements Creator<SettingsActor>{
		private static final long serialVersionUID = 1L;

		@Override
		public SettingsActor create() throws Exception {
			
			return new SettingsActor();
		}
	}
	
	
	public static void main(String[] args) {
		
		ActorSystem system=ActorSystem.create("SettingsActor");
		
		Props create = Props.create(SettingsActor.class, new PrarmCreator());
		
		ActorRef settingActor=system.actorOf(create, "SettingsActor");
		settingActor.tell("setting", ActorRef.noSender());
		
		final Deadline d = Duration.create(5, "nanoseconds").fromNow();
		
	}
	
	
	

}
