package com.nestoop.yelibar.akka.faulthander.api;

import akka.actor.ActorRef;

/**
 * ��ƽ������Ϣ
 * @author xbao
 *
 */
public class SenderMsgPair {
	
	public final ActorRef  sender;
	
	public final Object  message;
	
	


	public SenderMsgPair(ActorRef sender, Object message) {
		this.sender = sender;
		this.message = message;
	}

}

