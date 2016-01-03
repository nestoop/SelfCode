package com.nestoop.yelibar.org.rpc.client.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nestoop.yelibar.org.rpc.client.until.RpcClientConstant;

/**
 * 客户端发现注册器
 * @author Administrator
 *
 */
public class RpcClientServiceRegistry {
	
	private static final Logger logger = LoggerFactory.getLogger(RpcClientServiceRegistry.class);
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	private volatile List<String> dataList = new ArrayList<String>();
	
	private String registryAddress;
	 

	public RpcClientServiceRegistry() {
	}


	public RpcClientServiceRegistry(String registryAddress) {
		this.registryAddress = registryAddress;
		ZooKeeper zk = connectServer();
        if (zk != null) {
            watchNode(zk);
        }
	}
	
	//发现zookeeper中的服务
    public String discover() {
    	String logStr=null;
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                logStr=String.format("zookeeper 可以使用的个数：%s,zonde的数据是：%s", size,data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                logger.debug("zookeeper using random data: {}", data);
                logStr=String.format("zookeeper 可以使用的个数：%s,zonde的数据是：%s", size,data);
            }
        }
        logger.debug(logStr);
        return data;
    }
	 
	//创建链接
	private ZooKeeper connectServer() {
		logger.info("Rpc客户端链接创建zookeeper：");
        ZooKeeper zookeeper = null;
        try {
        	zookeeper = new ZooKeeper(registryAddress, RpcClientConstant.ZK_SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }

            });
            latch.await();
        } catch (IOException e) {
        	logger.error("链接创建zookeeper 出现异常：{}", e.getMessage());
        } catch (InterruptedException e) {
			e.printStackTrace();
		}
        return zookeeper;
    }
	//监控zookeeper的znode
	private void watchNode(final ZooKeeper zk) {
		logger.info("Rpc客户端监控zookeeper：");
        try {
//            List<String> nodeList = zk.getChildren(RpcClientConstant.ZK_REGISTRY_PATH, new Watcher() {
//                public void process(WatchedEvent event) {
//                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
//                        watchNode(zk);
//                    }
//                }
//            });
            List<String> nodeList = zk.getChildren(RpcClientConstant.ZK_REGISTRY_PATH,true);
            
            List<String> dataList = new ArrayList<String>();
            
            for (String node : nodeList) {
                byte[] bytes = zk.getData(RpcClientConstant.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(bytes));
            }
            logger.debug("node data: {}", dataList);
            this.dataList = dataList;
        } catch (KeeperException e) {
        	logger.error("watchNode KeeperException 出现异常信息：{}", e.getMessage());
        } catch (InterruptedException e) {
        	logger.error("watchNode InterruptedException出现异常信息：{}", e.getMessage());
			e.printStackTrace();
		}
    }

}
