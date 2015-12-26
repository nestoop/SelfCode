package com.nestoop.yelibar.akka.faulthander.util;
/**
 * 
 * 定义增长类
 */
public  class Increment{
	
	public final long n;
	
	public Increment(long n){
		this.n=n;
	}
	
	@Override
	public String toString(){
		
		return String.format("%s(%s)",getClass().getSimpleName(),n);
	}
}
