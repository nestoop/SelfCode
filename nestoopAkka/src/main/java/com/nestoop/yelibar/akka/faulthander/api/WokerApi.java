package com.nestoop.yelibar.akka.faulthander.api;

//WorkAPI ��ʾwork�Ķ���ָ��
public interface WokerApi {

	// ��������
	public static final Object START = "start";
	// ��ʼִ�з�������
	public static final Object DO = "Do";

	// �������class
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