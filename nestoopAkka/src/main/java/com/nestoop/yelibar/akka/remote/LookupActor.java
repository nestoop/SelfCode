package com.nestoop.yelibar.akka.remote;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.ReceiveTimeout;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;

public class LookupActor extends UntypedActor {
	
	final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

	private final String path;
	private ActorRef calculator = null;

	public LookupActor(String path) {
		this.path = path;
		sendIdentifyRequest();
	}

	private void sendIdentifyRequest() {
		// 按照path 路径寻找actor
		getContext().actorSelection(path).tell(new Identify(path), getSelf());
		// 3秒后调度，
		getContext()
				.system()
				.scheduler()
				.scheduleOnce(Duration.create(3, TimeUnit.SECONDS), getSelf(),
						ReceiveTimeout.getInstance(),
						getContext().dispatcher(), getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		logger.info("message is {}",message);
		//actor是否验证，
		if (message instanceof ActorIdentity) {
			calculator = ((ActorIdentity) message).getRef();
			if (calculator == null) {
				logger.error(String.format("Remote actor not available,because path is [%s]: ",path));
			} else {
				getContext().watch(calculator);
				getContext().become(active, true);
			}

		} else if (message instanceof ReceiveTimeout) {
			sendIdentifyRequest();

		} else {
			logger.debug("Not yet!!!");

		}

	}

	Procedure<Object> active = new Procedure<Object>() {
		@Override
		public void apply(Object message) {
			logger.info("Procedure apply message is {}",message);
			if (message instanceof MathOperationSample.Add) {
				// send message to server actor
				calculator.tell(message, getSelf());

			} else if (message instanceof MathOperationSample.AddResult) {
				MathOperationSample.AddResult result = (MathOperationSample.AddResult) message;
				logger.debug("Add result: %d + %d = %d\n", result.getN1(),
						result.getN2(), result.getResult());

			} else if (message instanceof Terminated) {
				logger.debug("Calculator terminated");
				sendIdentifyRequest();
				getContext().unbecome();

			} else if (message instanceof ReceiveTimeout) {
				// ignore

			} else {
				unhandled(message);
			}

		}
	};

}
