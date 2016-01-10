package com.nestoop.org.net.rpc.cluster.registry;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
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
	
	/**
	 * 创建zookeeper
	 * @return
	 */
	private ZooKeeper connectServer() {
		logger.debug("创建zookeeper....................");
		ZooKeeper zk=null;
		 try {
			zk=new ZooKeeper(registryAddress, RpcClusterConstant.ZK_SESSION_TIMEOUT, new RpClusterWatcher(latch));
		} catch (IOException e) {
			logger.debug("创建zookeeper出现异常 ,exception message:{}",e.getMessage());
			e.printStackTrace();
		}
		 try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
			String path=zookeeper.create(RpcClusterConstant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.debug("zookeeper创建Node:(path=>data) =>({} => {})", path, data);
		}catch(Exception e){
			logger.debug("zookeeper创建Node.出现异常，exception message:{}",e.getMessage());
			checkRootPathExits(zookeeper,RpcClusterConstant.ZK_REGISTRY_PATH,data);
		}
	}
	
	
	private void checkRootPathExits(ZooKeeper zookeeper,String rootPath,String data){
		logger.debug("Server 检查zookeeper 是否存在rootPath：{}",rootPath);
		
		try {
			//是否存在是否实时指定的rootpath
			Stat stat=zookeeper.exists(rootPath, true);
			logger.debug("Server 端检查zookeeper 是否存在rootPath：{}",rootPath);	
			if(stat ==null){
				logger.debug("Server 端创建在zookeeper 的rootPath节点：{}",rootPath);	
				zookeeper.create(rootPath, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	

}
