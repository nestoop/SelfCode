package com.nestoop.yelibar.akka.faulthander.util;

public class Entry {
	
	public final String key;
	
	public final long value;

	public Entry(String key, long value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("%s(%s,%s)",getClass().getSimpleName(),key,value);
	}
	
	

	
}
