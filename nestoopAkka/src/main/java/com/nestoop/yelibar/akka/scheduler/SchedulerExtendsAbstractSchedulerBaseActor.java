package com.nestoop.yelibar.akka.scheduler;

import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.AbstractSchedulerBase;
import akka.actor.Cancellable;
/**
 * �̳�ʵ��
 * @author Administrator
 *
 */
public class SchedulerExtendsAbstractSchedulerBaseActor extends AbstractSchedulerBase {
	/**
	 * ��ǰʱ��ļ���Ƕ���
	 */
	@Override
	public double maxFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * �ڼ��೤ʱ�俪ʼִ�У�ÿ���೤ʱ���ظ�ִ��
	 */
	@Override
	public Cancellable schedule(FiniteDuration initialDelay, FiniteDuration interval,
			Runnable runnable, ExecutionContext executor) {
		// TODO Auto-generated method stub
		return null;
	}
    /**
     * ֻ��ִ��һ�Σ��ڵ�ǰʱ��֮��೤ʱ�俪ʼִ�У�ֻ��ִ��һ��
     */
	@Override
	public Cancellable scheduleOnce(FiniteDuration interval, Runnable runnable, ExecutionContext executor) {
		// TODO Auto-generated method stub
		return null;
	}

}
