package com.nestoop.yelibar.akka.io;

import java.net.InetSocketAddress;


import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import akka.japi.Procedure;
import akka.util.ByteString;

public class AKKAClient extends UntypedActor{
	
	final InetSocketAddress remote;
	
	final ActorRef listener;
	
	public AKKAClient(InetSocketAddress remote,ActorRef listener){
		this.listener = listener;
		this.remote = remote;
		//创建实例 actor的tcp 实例
		final ActorRef tcp=Tcp.get(getContext().system()).manager();
		tcp.tell(TcpMessage.connect(remote),getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof CommandFailed) {
			listener.tell("failed", getSelf());
			getContext().stop(getSelf());
		} else if (message instanceof Connected) {
			listener.tell(message, getSelf());
			//tcpmessage
			getSender().tell(TcpMessage.register(getSelf()), getSelf());
			getContext().become(connected(getSender()));
		}
	}
	
	private Procedure<Object> connected(final ActorRef connect){
		
		return new Procedure<Object>() {
			@Override
			public void apply(Object message) throws Exception {
				System.out.println("Procedure apply message:"+message);
				if(message instanceof ByteString){
					connect.tell(TcpMessage.write((ByteString) message), getSelf());
				}else if(message instanceof CommandFailed){
					//系统的处理命令的socket 已经满了
				}else if(message instanceof Received){
					listener.tell(((Received) message).data(), getSelf());
				}else if(message.equals("close")){
					connect.tell(TcpMessage.close(),getSelf());
					
				}else if(message instanceof ConnectionClosed){
					getContext().stop(getSelf());
				}
					
				
			}
		};
	} 
	
	public static Props props(InetSocketAddress remote,ActorRef listener){
		
		return Props.create(AKKAClient.class,remote,listener);
	}

}
