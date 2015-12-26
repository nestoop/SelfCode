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
		
		//创建一个actor的引用
		ActorRef ref=system.actorOf(Props.create(TypeActorsExample.class),"TypeActorsExample"); 
		//typeActor 实现与ref actor的通信. 使用了TypedActor的代理模式
		extension.typedActorOf(new TypedProps<SquarerImpl>(Squarer.class,SquarerImpl.class),ref);
		
		ActorContext context = TypedActor.context();
		
		//代理模式 ,默认无参数构造器
		Squarer squarer=TypedActor.get(system).typedActorOf(new TypedProps<SquarerImpl>(Squarer.class,SquarerImpl.class));
		//来个有参数的 构造器，代理模式
	    Squarer otherSquarer =TypedActor.get(system).typedActorOf(new TypedProps<SquarerImpl>(Squarer.class,
	    	    new Creator<SquarerImpl>() {
	    	        public SquarerImpl create() { return new SquarerImpl("foo"); }
	    	     }),"name");
	    
	    //发送消息出去，自己寻找 不管了
	    squarer.squareDontCare(10);
	    //阻碍方式请求方式，发送消息
	    Option<Integer> option= otherSquarer.squareNowPlease(10);
	    //无阻碍请求响应发送消息
	    Future<Integer> future=squarer.square(10);
		
	    /**
	     * 停止请求的对象
	     */
	    //停止请求 
	    TypedActor.get(system).stop(squarer);
	    //直接更个小毒药丸
	    TypedActor.get(system).poisonPill(squarer);
	    
	    
	}
	

	@Override
	public void onReceive(Object arg0) throws Exception {
		
	}
	
	//接口
	public static interface Squarer{
		
	   void squareDontCare(int i); //自寻方式模式  和actor.tell
		 
	   Future<Integer> square(int i); //无阻碍请求和响应模式（ send-request-reply） 类似actor.ask
	 
	   Option<Integer> squareNowPlease(int i);//blocking send-request-reply  阻碍模式请求响应，请求出去，等待响应，和时间超时问题
	 
	   int squareNow(int i); //blocking send-request-reply
	}
	
	//实现
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
