package com.nestoop.yelibar.akka.scheduler;

import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.AbstractSchedulerBase;
import akka.actor.Cancellable;
/**
 * 继承实现
 * @author Administrator
 *
 */
public class SchedulerExtendsAbstractSchedulerBaseActor extends AbstractSchedulerBase {
	/**
	 * 当前时间的间隔是多少
	 */
	@Override
	public double maxFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * 在几多长时间开始执行，每隔多长时间重复执行
	 */
	@Override
	public Cancellable schedule(FiniteDuration initialDelay, FiniteDuration interval,
			Runnable runnable, ExecutionContext executor) {
		// TODO Auto-generated method stub
		return null;
	}
    /**
     * 只是执行一次，在当前时间之后多长时间开始执行，只是执行一次
     */
	@Override
	public Cancellable scheduleOnce(FiniteDuration interval, Runnable runnable, ExecutionContext executor) {
		// TODO Auto-generated method stub
		return null;
	}

}
