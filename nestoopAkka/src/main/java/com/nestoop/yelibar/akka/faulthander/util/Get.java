package com.nestoop.yelibar.akka.faulthander.util;

/**
 * Get �� ������key ��ȡActorOf
 */
public class Get {

	public final String key;

	public Get(String key) {
		this.key = key;
	}
	
	@Override
	public String toString() {

		return String.format("%s(%s)", Get.class.getSimpleName(), key);
	}

}
