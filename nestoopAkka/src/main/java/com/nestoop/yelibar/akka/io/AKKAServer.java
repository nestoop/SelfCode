package com.nestoop.yelibar.akka.io;

import java.net.InetSocketAddress;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.Tcp.Bound;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.TcpMessage;

/**
 * TcpµÄserver
 * @author Administrator
 *
 */
public class AKKAServer extends UntypedActor{
	
	final ActorRef manager;
	
	 public AKKAServer(ActorRef manager) {
		this.manager=manager;
	 }
	
	 public static Props props(ActorRef manager){
		 return Props.create(AKKAServer.class,manager);
	 }
	 
	 @Override
	 public void preStart() throws Exception {
		 final ActorRef tcp = Tcp.get(getContext().system()).manager();
		 tcp.tell(TcpMessage.bind(getSelf(),new InetSocketAddress("localhost", 9005), 100), getSelf());
	 }

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Bound){
			manager.tell(message, getSelf());
		}else if(message instanceof CommandFailed){
			getContext().stop(getSelf());
		}else if(message instanceof Connected){
			final Connected connected=(Connected) message;
			manager.tell(connected, getSelf());
			
			final ActorRef handler=getContext().actorOf(Props.create(SimplisticHandler.class));
			
			getSender().tell(TcpMessage.register(handler), getSelf());
		}
	}

}
