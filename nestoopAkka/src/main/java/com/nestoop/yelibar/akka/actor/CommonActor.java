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
	
	//Creating Actors with Props(Props��������Actor��������)
	final ActorRef unTypeActor=system.actorOf(Props.create(MyUnTypeActor.class).withDispatcher("systemActor-dispatcher"), "rootUntypeActor");
	//Creating Actors with Props(PropsĬ�Ϲ�������Actor��������)
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
				//���Դ�����Ϣ����
				
			}else{
				//���ܴ������Ϣ����
				unhandled(message);
			}
		}

		@Override
		public void preStart() throws Exception {
			//��ʼ���ĵط�
			super.preStart();
		}

		@Override
		public ActorRef getSelf() {
			// ���õ�ǰActor��Actor
			return super.getSelf();
		}

		@Override
		public ActorRef getSender() {
			//����������ض�����������Ϣ�ķ���Actor��ͨ������Ӧ��Actor.
			return super.getSender();
		}

		@Override
		public SupervisorStrategy supervisorStrategy() {
			// �˷��������Ǵ����� Actors�ĵط�
			return super.supervisorStrategy();
		}

		@Override
		public UntypedActorContext getContext() {
			/**
			 *������������ã� 
			 * 1��������Actors�Ĺ�������
			 * 2����ǰActor�Ĺ���
			 * 3����Actor�Ĵ���
			 * 4��������Actor
			 * 5:�����������
			 * 6����ջʵ���Ȳ��
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
