package com.nestoop.yelibar.akka.remote;

import com.nestoop.yelibar.akka.remote.MathOperationSample.Add;
import com.nestoop.yelibar.akka.remote.MathOperationSample.AddResult;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class CalculatorActor extends UntypedActor{
	
	final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object message) throws Exception {
		//判断传递过来的计算事件
		if(message instanceof MathOperationSample.Add){//两数增加操作
			MathOperationSample.Add add=(Add) message;
			int n1=add.getN1();
			int n2=add.getN2();
			logger.debug(String.format("MathOperationSample.Add,message.add(n1,n2) is (%s,%s) ",n1,n2));
			
			MathOperationSample.AddResult result=new AddResult(n1,n2,n1+n2);
			
			getSender().tell(result, getSelf());
			
		}else{
			unhandled(message);
			
		}
		
	}

}
