package com.yelibar.nestoop.disruptor.event.handler;

import java.util.UUID;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.yelibar.nestoop.disruptor.event.CodeEvent;

public class CodeEventHandler implements EventHandler<CodeEvent>,WorkHandler<CodeEvent>{
	

	public void onEvent(CodeEvent event, long sequence, boolean endOfBatch)throws Exception {
		this.onEvent(event);
	}

	public void onEvent(CodeEvent event) throws Exception {
		System.out.println("handler 处理.........................");
		event.setCode(UUID.randomUUID().toString());
	}

}
