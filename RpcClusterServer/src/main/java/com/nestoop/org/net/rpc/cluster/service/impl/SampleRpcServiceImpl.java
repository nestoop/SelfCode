package com.nestoop.org.net.rpc.cluster.service.impl;

import com.nestoop.org.net.rpc.cluster.service.SampleRpcService;
import com.nestoop.org.net.rpc.cluster.spring.RPCScaneServer;

@RPCScaneServer(SampleRpcService.class)
public class SampleRpcServiceImpl implements SampleRpcService {

	public String getSampleName(String sampleName) {
		
		return String.format("sampleNmae:%s",sampleName);
	}

}
