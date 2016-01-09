package com.nestoop.yelibar.akka.cluster.traffic;

import com.nestoop.yelibar.akka.cluster.traffic.TrafficMessage.TrafficMessageJob;
import com.nestoop.yelibar.akka.cluster.traffic.TrafficMessage.TrafficMessageResult;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class TrafficMessageBackEnd extends UntypedActor{
	
	public  final LoggingAdapter logger=Logging.getLogger(getContext().system(),this);
	
	//����һ���ֲ�ʽ
	Cluster cluster=Cluster.get(getContext().system());
	

	@Override
	public void postStop() throws Exception {
		cluster.subscribe(getSelf(), MemberUp.class);
	}

	@Override
	public void preStart() throws Exception {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		logger.info("���TrafficMessageBackEnd������Ϣ������Ϊ��msesage={}",message);
		if(message instanceof TrafficMessageJob){
			
			TrafficMessageJob job=(TrafficMessageJob) message;
			
			getSender().tell(new TrafficMessageResult(job.getText()), getSelf());
			
		}else if(message instanceof MemberUp){
			
			MemberUp memberUp =(MemberUp) message;
			
			register(memberUp.member());
			
		}else if(message instanceof CurrentClusterState){
			
			CurrentClusterState state=(CurrentClusterState) message;
			
			for(Member member:state.getMembers()){
				//ע�ᵱǰ��member��up״̬���Ž���ע�ᵽ���
				if(member.status().equals(MemberStatus.Up)){
					register(member);
				}
			}
			
		}else{
			unhandled(message);
		}
		
		
	}
	
	private void register(Member member){
		
		if(member.hasRole("frontend")){
			getContext().actorSelection(member.address()+"/user/frontend").tell(TrafficMessage.BACKEND_REGISTRATION,getSelf());
		}
		
	}

}
