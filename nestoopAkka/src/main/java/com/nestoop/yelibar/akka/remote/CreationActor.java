package com.nestoop.yelibar.akka.remote;

import com.nestoop.yelibar.akka.remote.MathOperationSample.AddResult;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 创建计算方式的Actor
 * @author Administrator
 *
 */
public class CreationActor extends UntypedActor {
	
	final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
	@Override
	public void onReceive(Object message) throws Exception {
		logger.info("message is {}",message);
		if(message instanceof MathOperationSample.MathOperation){ //计算方式进来
			ActorRef calculator=getContext().actorOf(Props.create(CalculatorActor.class));
			calculator.tell(message, getSelf());
		}else if(message instanceof MathOperationSample.AddResult){//计算结果结果进来
			MathOperationSample.AddResult result=(AddResult) message;
			logger.info("相加结果进来了,message.n1 :'"+result.getN1()+"',message.n2:'"+result.getN2()+"',message.result:'"+result.getResult()+"'");
			//停止发送
			getContext().stop(getSender());
		}else{
			unhandled(message);
		}
		
	}

}
