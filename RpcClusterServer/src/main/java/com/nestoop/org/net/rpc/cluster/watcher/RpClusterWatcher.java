package com.nestoop.org.net.rpc.cluster.watcher;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class RpClusterWatcher implements Watcher {
	
	private CountDownLatch latch;
	

	public RpClusterWatcher(CountDownLatch latch) {
		this.latch = latch;
	}

	
	public void process(WatchedEvent event) {
		 if (event.getState() == Event.KeeperState.SyncConnected) {
             latch.countDown();
         }
		
	}
	
	

}
