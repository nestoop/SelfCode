package com.nestoop.org.net.rpc.cluster.registry;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nestoop.org.net.rpc.cluster.constant.RpcClusterConstant;
import com.nestoop.org.net.rpc.cluster.watcher.RpClusterWatcher;

/**
 * RPC 注册服务
 * @author xbao
 *
 */
public class RpcServiceRegistry {
	
	private static final Logger logger = LoggerFactory.getLogger(RpcServiceRegistry.class);
	//
	private String registryAddress;
	
	private CountDownLatch latch = new CountDownLatch(1);

	public RpcServiceRegistry(String registryAddress) {
		this.registryAddress = registryAddress;
	}
	
	public  void  	register(String data){
		
		if(data !=null){
			//创建zookeeper
			ZooKeeper zookeeper=connectServer();
			//注册到zookeeper
			createNode(zookeeper,data);
		}
	}
	
	private ZooKeeper connectServer() {
		logger.debug("创建zookeeper....................");
		ZooKeeper zk=null;
		 try {
			zk=new ZooKeeper(registryAddress, RpcClusterConstant.ZK_SESSION_TIMEOUT, new RpClusterWatcher(latch));
		} catch (IOException e) {
			logger.debug("创建zookeeper出现异常 ,exception message:{}",e.getMessage());
			e.printStackTrace();
		}
		return zk;
	}
	
	//创建znode
	private void createNode(ZooKeeper zookeeper,String data){
		logger.debug("zookeeper创建znode....................");
		try{
			byte[]  bytes=data.getBytes();
			//path
			String path=zookeeper.create(RpcClusterConstant.ZK_REGISTRY_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			logger.debug("zookeeper创建Node:(path=>data) =>({} => {})", path, data);
		}catch(Exception e){
			logger.debug("zookeeper创建Node.出现异常，exception message:{}",e.getMessage());
		}
	}
	
	

}
