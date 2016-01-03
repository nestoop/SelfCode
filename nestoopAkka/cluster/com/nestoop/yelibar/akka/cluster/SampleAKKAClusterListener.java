package com.nestoop.yelibar.akka.cluster;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 
 * akka cluster 监听者
 * @author xbao
 *
 */ 
public class SampleAKKAClusterListener extends UntypedActor {
	//日志 组件
	public final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
	//分布式
	Cluster cluster = Cluster.get(getContext().system());
	@Override
	public void onReceive(Object message) throws Exception {
		logger.info("Member is  {}",message);	
		if (message instanceof MemberUp) {
			MemberUp mUp = (MemberUp) message;
			logger.info("Member is Up: {}", mUp.member());
		}else if (message instanceof CurrentClusterState) {
		      CurrentClusterState state = (CurrentClusterState) message;
		      logger.info("Current members: {}", state.members());
	    } else if (message instanceof UnreachableMember) {
			UnreachableMember mUnreachable = (UnreachableMember) message;
			logger.info("Member detected as unreachable: {}",mUnreachable.member());

		} else if (message instanceof MemberRemoved) {
			MemberRemoved mRemoved = (MemberRemoved) message;
			logger.info("Member is Removed: {}", mRemoved.member());

		} else if (message instanceof MemberEvent) {
			// ignore
		} else {
			unhandled(message);
		}

	}
	
	@Override
	public void postStop() throws Exception {
		//actor 停止
		logger.debug("actor is  just stop!!!,Cluster 退出消息订阅");
		//退出消息订阅
		cluster.unsubscribe(getSelf());
	}
	@Override
	public void preStart() throws Exception {
		//订阅消息
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
	}
	
	

	
	

}
