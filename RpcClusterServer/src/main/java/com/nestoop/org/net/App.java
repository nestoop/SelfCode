package com.nestoop.org.net;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.nestoop.org.net.rpc.cluster.server.RpcServer;


/**
 * Hello world!
 *
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class App{
	
    public static void main(String[] args) throws Exception {
    	ClassPathXmlApplicationContext applicationContext =new ClassPathXmlApplicationContext("spring.xml");
    	RpcServer rpcServer=(RpcServer) applicationContext.getBean("rpcServer");
    	rpcServer.start();
        SpringApplication.run(new Object[]{App.class}, args);
    } 
}
