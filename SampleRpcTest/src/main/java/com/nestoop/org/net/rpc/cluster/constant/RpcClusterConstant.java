package com.nestoop.org.net.rpc.cluster.constant;

public interface RpcClusterConstant {
	
	//zookeeper的session 超时
	public static final int ZK_SESSION_TIMEOUT = 5000;
	//zookeeper
	public static final String ZK_REGISTRY_PATH = "/rpc-zookeeper-registry";
	//zookeeper 的data
	public static final String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";

}
