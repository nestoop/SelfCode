package com.nestoop.yelibar.akka.cluster.traffic;

import java.io.Serializable;

/**
 * ͨ����Ϣ
 * @author xbao
 *
 */
public class TrafficMessage {
	
	public static final String BACKEND_REGISTRATION = "BackendRegistration";
	
	//��Ϣ�����֮һ-->ͨ��job �� TrafficMessageJob
	public static class TrafficMessageJob implements Serializable{

		private static final long serialVersionUID = 1L;
		
		private final String text;
		
		public TrafficMessageJob(String text){
			this.text=text;
		}
		
		public String getText(){
			return text;
		}
		
	}
	
	//��Ϣ�����֮һ-->ͨ�Ž�� �� TrafficMessageResult
	public static class TrafficMessageResult implements Serializable{
		
		private static final long serialVersionUID = 1L;
		
		private final String text;
		
		public TrafficMessageResult(String text){
			this.text=text;
		}
		
		public String getText(){
			return text;
		}

		@Override
		public String toString() {
			
			return String.format("��Ϣ�����֮һ-->ͨ�Ž�� �� TrafficMessageResult(text=%s)",text);
		}
		
	}
	//��Ϣ�����֮һ-->ͨ��ʧ�� �� TrafficMessageFail
	public static class TrafficMessageFail  implements Serializable{

		private static final long serialVersionUID = 1L;
		
		private final String reason;
		
		private final TrafficMessageJob messageJob;
		
		public TrafficMessageFail(String reason,TrafficMessageJob messageJob){
			this.reason=reason;
			this.messageJob=messageJob;
		}
		
		public TrafficMessageJob getMessageJob(){
			return this.messageJob;
		}
		
		public String getReason(){
			return this.reason;
		}
		
		@Override
		public String toString(){
			return String.format("��Ϣ�����֮һ-->ͨ��ʧ�� �� TrafficMessageFail.reason=%s",reason);
		}
		
		
	}
	
	

}
