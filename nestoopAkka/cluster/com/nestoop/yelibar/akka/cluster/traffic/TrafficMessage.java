package com.nestoop.yelibar.akka.cluster.traffic;

import java.io.Serializable;

/**
 * 通信消息
 * @author xbao
 *
 */
public class TrafficMessage {
	
	public static final String BACKEND_REGISTRATION = "BackendRegistration";
	
	//消息的组件之一-->通信job ： TrafficMessageJob
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
	
	//消息的组件之一-->通信结果 ： TrafficMessageResult
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
			
			return String.format("消息的组件之一-->通信结果 ： TrafficMessageResult(text=%s)",text);
		}
		
	}
	//消息的组件之一-->通信失败 ： TrafficMessageFail
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
			return String.format("消息的组件之一-->通信失败 ： TrafficMessageFail.reason=%s",reason);
		}
		
		
	}
	
	

}
