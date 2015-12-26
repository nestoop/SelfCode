package com.nestoop.yelibar.akka.scheduler;

import scala.Function0;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Scheduler;
/**
 * 接口实现自己的调度器Scheduler
 * @author xbao
 *
 */
public class ImplScheulerInterfaceActor implements Scheduler {

	@Override
	public double maxFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cancellable schedule(FiniteDuration arg0, FiniteDuration arg1,
			Function0<BoxedUnit> arg2, ExecutionContext arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cancellable schedule(FiniteDuration arg0, FiniteDuration arg1,
			Runnable arg2, ExecutionContext arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cancellable schedule(FiniteDuration arg0, FiniteDuration arg1,
			ActorRef arg2, Object arg3, ExecutionContext arg4, ActorRef arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorRef schedule$default$6(FiniteDuration arg0,
			FiniteDuration arg1, ActorRef arg2, Object arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cancellable scheduleOnce(FiniteDuration arg0,
			Function0<BoxedUnit> arg1, ExecutionContext arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cancellable scheduleOnce(FiniteDuration arg0, Runnable arg1,
			ExecutionContext arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cancellable scheduleOnce(FiniteDuration arg0, ActorRef arg1,
			Object arg2, ExecutionContext arg3, ActorRef arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorRef scheduleOnce$default$5(FiniteDuration arg0, ActorRef arg1,
			Object arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
