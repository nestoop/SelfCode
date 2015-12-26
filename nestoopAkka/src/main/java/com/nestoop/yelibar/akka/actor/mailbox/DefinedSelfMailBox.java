package com.nestoop.yelibar.akka.actor.mailbox;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.typesafe.config.Config;

import scala.Option;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.Envelope;
import akka.dispatch.MailboxType;
import akka.dispatch.MessageQueue;
import akka.dispatch.ProducesMessageQueue;

public class DefinedSelfMailBox implements MailboxType,ProducesMessageQueue<DefinedSelfMailBox.DefinedMessageQueue>{
	
	public final DispatcherConfigMailBoxExtension.ConfigSetting settings;
	
	public final Config config;
	
	public DefinedSelfMailBox(DispatcherConfigMailBoxExtension.ConfigSetting settings, Config config) {
		this.settings=settings;
		new DispatcherConfigMailBoxExtension(config);
		this.config=config;
	}
	
	public static class DefinedMessageQueue implements MessageQueue,DefinedUnboundedJMessageQueueSemantics{
		
		private final Queue<Envelope> queue = new ConcurrentLinkedQueue<Envelope>();

		@Override
		public void cleanUp(ActorRef ower, MessageQueue deadLetters) {
			
			for(Envelope handler:queue){
				deadLetters.enqueue(ower, handler);
			}
		}

		@Override
		public Envelope dequeue() {
			// ɾ�����е�����
			return queue.poll();
		}

		@Override
		public void enqueue(ActorRef receiver, Envelope handler) {
			queue.offer(handler);
			
		}

		@Override
		public boolean hasMessages() {
			// ���������Ƿ������Ϣ
			return !queue.isEmpty();
		}

		@Override
		public int numberOfMessages() {
			// ���е���Ϣ����
			return queue.size();
		}
		
		
	}

	@Override
	public MessageQueue create(Option<ActorRef> ower, Option<ActorSystem> system) {
		return new DefinedMessageQueue();
	}
	

	
	

}
