package com.nestoop.yelibar.akka.scheduler;

import akka.actor.Cancellable;

/**
 * ȡ������
 * @author xbao
 *
 */
public class CancellableActor     implements Cancellable{
   /**
    * ��ֹ����
    */
	@Override
	public boolean cancel() {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * ����Ƿ��Ѿ���ֹ����
	 */
	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

}
