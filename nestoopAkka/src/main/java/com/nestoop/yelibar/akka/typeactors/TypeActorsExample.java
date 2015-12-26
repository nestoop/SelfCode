package com.nestoop.yelibar.akka.typeactors;


import scala.concurrent.Future;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.TypedActor;
import akka.actor.TypedActorExtension;
import akka.actor.TypedProps;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.japi.Creator;
import akka.japi.Option;

public class TypeActorsExample extends UntypedActor {
	
	public static void main(String[] args) {
		ActorSystem system=ActorSystem.create("system");
		
		TypedActorExtension extension =TypedActor.get(system);
		
		//����һ��actor������
		ActorRef ref=system.actorOf(Props.create(TypeActorsExample.class),"TypeActorsExample"); 
		//typeActor ʵ����ref actor��ͨ��. ʹ����TypedActor�Ĵ���ģʽ
		extension.typedActorOf(new TypedProps<SquarerImpl>(Squarer.class,SquarerImpl.class),ref);
		
		ActorContext context = TypedActor.context();
		
		//����ģʽ ,Ĭ���޲���������
		Squarer squarer=TypedActor.get(system).typedActorOf(new TypedProps<SquarerImpl>(Squarer.class,SquarerImpl.class));
		//�����в����� ������������ģʽ
	    Squarer otherSquarer =TypedActor.get(system).typedActorOf(new TypedProps<SquarerImpl>(Squarer.class,
	    	    new Creator<SquarerImpl>() {
	    	        public SquarerImpl create() { return new SquarerImpl("foo"); }
	    	     }),"name");
	    
	    //������Ϣ��ȥ���Լ�Ѱ�� ������
	    squarer.squareDontCare(10);
	    //�谭��ʽ����ʽ��������Ϣ
	    Option<Integer> option= otherSquarer.squareNowPlease(10);
	    //���谭������Ӧ������Ϣ
	    Future<Integer> future=squarer.square(10);
		
	    /**
	     * ֹͣ����Ķ���
	     */
	    //ֹͣ���� 
	    TypedActor.get(system).stop(squarer);
	    //ֱ�Ӹ���С��ҩ��
	    TypedActor.get(system).poisonPill(squarer);
	    
	    
	}
	

	@Override
	public void onReceive(Object arg0) throws Exception {
		
	}
	
	//�ӿ�
	public static interface Squarer{
		
	   void squareDontCare(int i); //��Ѱ��ʽģʽ  ��actor.tell
		 
	   Future<Integer> square(int i); //���谭�������Ӧģʽ�� send-request-reply�� ����actor.ask
	 
	   Option<Integer> squareNowPlease(int i);//blocking send-request-reply  �谭ģʽ������Ӧ�������ȥ���ȴ���Ӧ����ʱ�䳬ʱ����
	 
	   int squareNow(int i); //blocking send-request-reply
	}
	
	//ʵ��
	public static class SquarerImpl implements Squarer {
		
		private  String name;
		
		public SquarerImpl(){
			name="default";
		}

		public SquarerImpl(String name) {
			this.name = name;
		}

		@Override
		public void squareDontCare(int i) {
			int seq=i*i;
			
		}

		@Override
		public Future<Integer> square(int i) {
			return Futures.successful(i*i);
		}

		@Override
		public Option<Integer> squareNowPlease(int i) {
			return Option.some(i*i);
		}

		@Override
		public int squareNow(int i) {
			return i*i;
		}
		
	}
	

}
