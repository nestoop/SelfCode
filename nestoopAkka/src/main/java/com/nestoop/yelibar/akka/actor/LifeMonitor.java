package com.nestoop.yelibar.akka.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

/**
 * 生命周期监控
 * 
 * @author xbao
 *
 */
public class LifeMonitor {
	
	public static void main(String[] args) {
		final ActorSystem system=ActorSystem.create();
		//Creating Actors with Props(Props默认构造器的Actor创建方法)
		final ActorRef watchActor=system.actorOf(Props.create(WatchActor.class), "WatchActor");
		
		watchActor.tell("kill", watchActor);
	}

	public static class WatchActor extends UntypedActor {

		final ActorRef child = this.getContext().actorOf(Props.empty(), "child");
		{
			this.getContext().watch(child); // <-- this is the only call needed
											// for registration
		}
		ActorRef lastSender = getContext().system().deadLetters();

		@Override
		public void onReceive(Object message) throws Exception {
			System.out.println("message:{}"+message);
			if (message.equals("kill")) {
				System.out.println("kill.................");
				getContext().stop(child);
				lastSender = getSender();
			} else if (message instanceof Terminated) {
				System.out.println("Terminated.............");
				final Terminated t = (Terminated) message;
				if (t.getActor() == child) {
					lastSender.tell("finished",getSelf());
				}
			} else {
				System.out.println("unhandled.............");
				unhandled(message);
			}
		}

	}

}
