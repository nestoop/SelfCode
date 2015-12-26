package com.nestoop.yelibar.akka.faulthander.api;

//WorkAPI 表示work的动作指令
public interface WokerApi {

	// 定义启动
	public static final Object START = "start";
	// 开始执行发送任务
	public static final Object DO = "Do";

	// 定义进度class
	public static class Progress {

		public static double percent;

		public Progress(double percent) {
			this.percent = percent;
		}

		// tostring()
		public String toString() {

			return String.format("%s(%s)", getClass().getSimpleName(), percent);
		}

	}

}