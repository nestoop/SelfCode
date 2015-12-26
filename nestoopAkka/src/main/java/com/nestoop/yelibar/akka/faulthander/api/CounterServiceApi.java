package com.nestoop.yelibar.akka.faulthander.api;

/**
 * 
 * ������������API
 * 
 * @author xbao
 *
 */

public  interface CounterServiceApi {

	public static final Object GetCurrentCount = "GetCurrentCount";

	// ���嵱ǰ�ļ�������
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