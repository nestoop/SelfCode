package com.nestoop.yelibar.akka.faulthander.exception;

//������������Ƿ�������
public class ServiceUnavailable extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceUnavailable(String message) {
		super(message);
	}

}