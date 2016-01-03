package com.nestoop.yelibar.org.rpc.client.until;

public class RpcClientConstant {
	
	//zookeeper的session 超时
		public static final int ZK_SESSION_TIMEOUT = 5000;
		//zookeeper
		public static final String ZK_REGISTRY_PATH = "/rpc-zookeeper-registry";
		//zookeeper 的data
		public static final String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";

}
