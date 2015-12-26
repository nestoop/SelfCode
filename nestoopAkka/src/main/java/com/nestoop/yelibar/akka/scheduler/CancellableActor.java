package com.nestoop.yelibar.akka.scheduler;

import akka.actor.Cancellable;

/**
 * 取消调度
 * @author xbao
 *
 */
public class CancellableActor     implements Cancellable{
   /**
    * 终止调度
    */
	@Override
	public boolean cancel() {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * 检查是否已经终止调度
	 */
	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

}
