package com.nestoop.yelibar.akka.remote;

import com.nestoop.yelibar.akka.remote.MathOperationSample.AddResult;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * �������㷽ʽ��Actor
 * @author Administrator
 *
 */
public class CreationActor extends UntypedActor {
	
	final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
	@Override
	public void onReceive(Object message) throws Exception {
		logger.info("message is {}",message);
		if(message instanceof MathOperationSample.MathOperation){ //���㷽ʽ����
			ActorRef calculator=getContext().actorOf(Props.create(CalculatorActor.class));
			calculator.tell(message, getSelf());
		}else if(message instanceof MathOperationSample.AddResult){//�������������
			MathOperationSample.AddResult result=(AddResult) message;
			logger.info("��ӽ��������,message.n1 :'"+result.getN1()+"',message.n2:'"+result.getN2()+"',message.result:'"+result.getResult()+"'");
			//ֹͣ����
			getContext().stop(getSender());
		}else{
			unhandled(message);
		}
		
	}

}
