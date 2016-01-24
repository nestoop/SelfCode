package com.yelibar.nestoop.disruptor.event;

import com.lmax.disruptor.EventFactory;

public class CodeEvent {
	
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	public static final EventFactory<CodeEvent> EVENT_FACTORY=new EventFactory<CodeEvent>() {
		
		public CodeEvent newInstance() {
			return new CodeEvent();
		}
	};

	

}
