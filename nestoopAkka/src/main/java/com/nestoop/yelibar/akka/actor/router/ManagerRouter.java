package com.nestoop.yelibar.akka.actor.router;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;





import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

/**
 * 在Actor中如何管理路由
 * @author Administrator
 *
 */
public class ManagerRouter {
	
	public static void main(String[] args) {
		
		ActorSystem system =ActorSystem.create("system");
		
		ActorRef master=system.actorOf(Props.create(Master.class),"master");
		
		ActorRef worker=system.actorOf(Props.create(Worker.class),"worker");
		
		master.tell(worker,worker);
		
	}
	
	
	
	
	public static class Master extends UntypedActor{
		
		//定义一个Router
		
		final Router router;
		
		//静态赋值
		
		{
			List<Routee> routees=new ArrayList<Routee>();
			for (int i = 0; i < 5; i++) {
				ActorRef r = getContext().actorOf(Props.create(Worker.class));
				getContext().watch(r);
				routees.add(new ActorRefRoutee(r));
			}
			router = new Router(new RoundRobinRoutingLogic(), routees);
		}

		@Override
		public void onReceive(Object message) throws Exception {
			
			System.out.println(String.format("message,(%s)",message));
			
			if(message  instanceof Worker){
				
				router.route(message,getSender());
				
			}else if(message instanceof Terminated){
				router.removeRoutee(((Terminated) message).actor());
				ActorRef r = getContext().actorOf(Props.create(Worker.class));
				getContext().watch(r);
				router.addRoutee(new ActorRefRoutee(r));
			}
			
			
		}
		
		
	}
	public static class Worker extends UntypedActor implements Serializable{
		
		private static final long serialVersionUID = 1L;
		
		public String payLoad;
		
		public Worker(){};
		
		

		@Override
		public void onReceive(Object message) throws Exception {
			// TODO Auto-generated method stub
			
		}
		
	}

}
