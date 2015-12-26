package com.nestoop.yelibar.akka.actor;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorContext;

public class CommonActor {
	
	final ActorSystem system=ActorSystem.create();
	
	//Creating Actors with Props(Props带参数的Actor创建方法)
	final ActorRef unTypeActor=system.actorOf(Props.create(MyUnTypeActor.class).withDispatcher("systemActor-dispatcher"), "rootUntypeActor");
	//Creating Actors with Props(Props默认构造器的Actor创建方法)
	final ActorRef abstractActor=system.actorOf(Props.create(MyAbstractActor.class), "rootAbstractActor");
	
	/**
	 * extends UntypedActor
	 * @author xbao
	 *
	 */
	public static class MyUnTypeActor extends UntypedActor{

		@Override
		public void onReceive(Object message) throws Exception {
			
			if(message instanceof String){
				//可以处理消息过程
				
			}else{
				//不能处理的消息过程
				unhandled(message);
			}
		}

		@Override
		public void preStart() throws Exception {
			//初始化的地方
			super.preStart();
		}

		@Override
		public ActorRef getSelf() {
			// 引用当前Actor的Actor
			return super.getSelf();
		}

		@Override
		public ActorRef getSender() {
			//这个方法返回定义最后接受消息的发送Actor，通常是响应的Actor.
			return super.getSender();
		}

		@Override
		public SupervisorStrategy supervisorStrategy() {
			// 此方法返回是处理子 Actors的地方
			return super.supervisorStrategy();
		}

		@Override
		public UntypedActorContext getContext() {
			/**
			 *这个方法的作用： 
			 * 1：创建子Actors的工厂方法
			 * 2：当前Actor的归属
			 * 3：父Actor的处理
			 * 4：处理子Actor
			 * 5:监控生命周期
			 * 6：在栈实现热插拔
			 */
			return super.getContext();
		}
		
	}
	/**
	 * extends AbstractActor
	 * @author xbao
	 *
	 */
	public static class MyAbstractActor extends AbstractActor{

		@Override
		public PartialFunction<Object, BoxedUnit> receive() {
			// TODO Auto-generated method stub
			return super.receive();
		}

		
	}
	
}
