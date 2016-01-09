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
		logger.info("ǰ��TrafficMessageFrontend������Ϣ������Ϊ��msesage={}",message);
		if(message instanceof TrafficMessageJob && backends.isEmpty() ){
			
			logger.info("ǰ��TrafficMessageFronten��û ���յ�������Ϣ.........................");
			
			TrafficMessageJob messageJob=(TrafficMessageJob) message;
			
			getSender().tell(new TrafficMessageFail("TrafficMessageFrontend �����ã�����Եȵ�һС�ᣬ��.........", messageJob), getSelf());
			
		}else if(message instanceof TrafficMessageJob){
			TrafficMessageJob messageJob=(TrafficMessageJob) message;
			//job������
			jobCounter++;
			logger.info("ǰ��TrafficMessageFronten �Ѿ����յ�������Ϣ ,�������Ĵ�С:{} ,backends.size:{}..................",jobCounter,backends.size());
			backends.get(jobCounter % backends.size()).forward(messageJob,getContext());
			
		}else if(message instanceof String && message.equals(TrafficMessage.BACKEND_REGISTRATION)){
			logger.info("ǰ��TrafficMessageFronten �Ѿ����յ�������ϢΪ:{}",message);
			getContext().watch(getSelf());
			backends.add(getSender());
		}else if (message instanceof Terminated){
			logger.info("ǰ��TrafficMessageFronten �Ѿ����յ�������ϢΪ:Terminated");
			//����Ƴ���actor
			Terminated terminated=(Terminated) message;
			backends.remove(terminated.actor());
		}else{
			unhandled(message);
		}
	}

}
