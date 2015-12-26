package com.nestoop.yelibar.akka.futures;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class AKKAFutures {
	
	
	public static void main(String[] args) {
		ActorSystem system=ActorSystem.create("system");
		//		ActorSystem.dispatcher()
		ExecutionContext executionContext=system.dispatcher();
		
		Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));
		
		ActorRef futuresActor=system.actorOf(Props.create(FuturesActorTemp.class),"FuturesActorTemp");
		//ActorSystem 方式
		Future<Object> future_ActorSystem=Patterns.ask(futuresActor, "发送消息，再过5s就超时了", timeout);
				
		Patterns.pipe(future_ActorSystem, executionContext).to(futuresActor);
		
		
		//ExecutionContexts 工厂方式ExecutionContext
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		ExecutionContext  ec=ExecutionContexts.fromExecutorService(executorService);
		
		//使用ExecutionContexts 工厂方式创建future
		Future<String> future=Futures.successful("foo");
		
		future.onSuccess(new PrintResult<String>(), ec);
		//关闭程序
		executorService.shutdown();
		
		
		
	}
	
	public static class PrintResult<T> extends OnSuccess<T>{

		@Override
		public void onSuccess(T t) throws Throwable {
			System.out.println("success=="+t);
			
		}
		
	}
	
	
	public static class FuturesActorTemp extends UntypedActor{

		@Override
		public void onReceive(Object message) throws Exception {
			System.out.println("message=="+message);
			
		}
		
		
	}
	
	
	

}
