package com.nestoop.yelibar.akka.faulthander.api;

/**
 * 
 * 定义计数服务的API
 * 
 * @author xbao
 *
 */

public  interface CounterServiceApi {

	public static final Object GetCurrentCount = "GetCurrentCount";

	// 定义当前的计数的类
	public static class CurrentCount {

		public final String key;

		public final long count;

		public CurrentCount(String key, long count) {
			this.key = key;
			this.count = count;
		}

		public String toString() {

			return String.format("%s(%s,%s)", getClass().getSimpleName(), key,count);
		}

	}

}