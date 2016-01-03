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
 * akka cluster ������
 * @author xbao
 *
 */ 
public class SampleAKKAClusterListener extends UntypedActor {
	//��־ ���
	public final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
	//�ֲ�ʽ
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
		//actor ֹͣ
		logger.debug("actor is  just stop!!!,Cluster �˳���Ϣ����");
		//�˳���Ϣ����
		cluster.unsubscribe(getSelf());
	}
	@Override
	public void preStart() throws Exception {
		//������Ϣ
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
	}
	
	

	
	

}
