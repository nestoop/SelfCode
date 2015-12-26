package com.nestoop.yelibar.akka.faulthander.exception;

//定义计数服务是否在运行
public class ServiceUnavailable extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceUnavailable(String message) {
		super(message);
	}

}