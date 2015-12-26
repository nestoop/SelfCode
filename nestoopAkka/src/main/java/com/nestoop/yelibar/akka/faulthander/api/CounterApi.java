package com.nestoop.yelibar.akka.faulthander.api;

import akka.actor.ActorRef;

/**
 * ¼ÆËãÆ÷APi
 * @author Administrator
 *
 */
public interface CounterApi{
	
	
	
	public static class UseStorage{
		
		public final ActorRef storage;
		
		public UseStorage(ActorRef storage){
			this.storage=storage;
		}
		
		public String toString(){
			
			return String.format("%s(%s)",getClass().getSimpleName(),storage);
		}
	}
	
}