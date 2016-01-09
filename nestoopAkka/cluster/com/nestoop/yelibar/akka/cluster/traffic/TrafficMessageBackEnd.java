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
	
	//定义一个分布式
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
		logger.info("后端TrafficMessageBackEnd接收消息的类型为：msesage={}",message);
		if(message instanceof TrafficMessageJob){
			
			TrafficMessageJob job=(TrafficMessageJob) message;
			
			getSender().tell(new TrafficMessageResult(job.getText()), getSelf());
			
		}else if(message instanceof MemberUp){
			
			MemberUp memberUp =(MemberUp) message;
			
			register(memberUp.member());
			
		}else if(message instanceof CurrentClusterState){
			
			CurrentClusterState state=(CurrentClusterState) message;
			
			for(Member member:state.getMembers()){
				//注册当前的member是up状态，才进行注册到后端
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
