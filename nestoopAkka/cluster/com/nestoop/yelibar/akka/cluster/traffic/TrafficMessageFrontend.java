package com.nestoop.yelibar.akka.cluster.traffic;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.nestoop.yelibar.akka.cluster.traffic.TrafficMessage.TrafficMessageJob;
import com.nestoop.yelibar.akka.cluster.traffic.TrafficMessage.TrafficMessageFail;

public class TrafficMessageFrontend extends UntypedActor {
	
	public  final LoggingAdapter logger=Logging.getLogger(getContext().system(),this);
	
	List<ActorRef> backends=new ArrayList<ActorRef>();
	public int jobCounter=0;
	@Override
	public void onReceive(Object message) throws Exception {
		logger.info("前端TrafficMessageFrontend接收消息的类型为：msesage={}",message);
		if(message instanceof TrafficMessageJob && backends.isEmpty() ){
			
			logger.info("前端TrafficMessageFronten还没 接收到接收消息.........................");
			
			TrafficMessageJob messageJob=(TrafficMessageJob) message;
			
			getSender().tell(new TrafficMessageFail("TrafficMessageFrontend 不可用，你可以等等一小会，亲.........", messageJob), getSelf());
			
		}else if(message instanceof TrafficMessageJob){
			TrafficMessageJob messageJob=(TrafficMessageJob) message;
			//job计数器
			jobCounter++;
			logger.info("前端TrafficMessageFronten 已经接收到接收消息 ,计数器的大小:{} ,backends.size:{}..................",jobCounter,backends.size());
			backends.get(jobCounter % backends.size()).forward(messageJob,getContext());
			
		}else if(message instanceof String && message.equals(TrafficMessage.BACKEND_REGISTRATION)){
			logger.info("前端TrafficMessageFronten 已经接收到接收消息为:{}",message);
			getContext().watch(getSelf());
			backends.add(getSender());
		}else if (message instanceof Terminated){
			logger.info("前端TrafficMessageFronten 已经接收到接收消息为:Terminated");
			//后端移除此actor
			Terminated terminated=(Terminated) message;
			backends.remove(terminated.actor());
		}else{
			unhandled(message);
		}
	}

}
