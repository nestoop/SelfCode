package com.nestoop.yelibar.org.app;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nestoop.org.net.rpc.cluster.service.SampleRpcService;
import com.nestoop.yelibar.org.rpc.client.proxy.RpcProxy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class RpcClientTest {
	
	 @Autowired
	 private RpcProxy rpcProxy;
	 
	@Test
	public void helloTest() {
		SampleRpcService sampleService = rpcProxy.createRpcService(SampleRpcService.class);
		String result = sampleService.getSampleName("ketty");
		System.out.println("------------------------------------------rpc client result:"+result);
	}

}
