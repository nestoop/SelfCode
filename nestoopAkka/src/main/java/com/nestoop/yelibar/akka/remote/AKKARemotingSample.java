package com.nestoop.yelibar.akka.remote;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.ExtensionIdProvider;
import akka.actor.UntypedActor;

import com.typesafe.config.Config;

/**
 * akka远程调用 实例
 * @author Administrator
 *
 */
public class AKKARemotingSample {
	
	
	
	public static class SampleCalculatorActor extends UntypedActor{

		@Override
		public void onReceive(Object message) throws Exception {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	
	
	
	/**
	 * 
	 * 自定义config读取类   。。。。。。。。。。。。。。。。。。。start
	 * @author Administrator
	 *
	 */
	
	public static class RemoteSettingsImpl implements Extension{
		//发布
		public  final String PROVIDER;
		//主机
		public  final String HOSTNAME;
		//端口
		public  final String PORT;
		
		
		public RemoteSettingsImpl(Config config) {
			
//			config.atPath("resources/application.conf");
			//发布者
			PROVIDER=config.getString("akka.actor.provider");
			//主机
			HOSTNAME=config.getString("akka.remote.netty.hostname");
			//写入
			PORT=config.getString("akka.remote.netty.port");
			
		}
		
	}
	
	public static class RemoteSettings extends AbstractExtensionId<RemoteSettingsImpl> implements ExtensionIdProvider {
	    public final static RemoteSettings SettingsProvider = new RemoteSettings();
	 
	    public RemoteSettings lookup() {
	      return RemoteSettings.SettingsProvider;
	    }
	 
	    public RemoteSettingsImpl createExtension(ExtendedActorSystem system) {
	      return new RemoteSettingsImpl(system.settings().config().atPath("resources/remote.conf"));
	    }
	}
	/**
	 * 
	 * 自定义config读取类   。。。。。。。。。。。。。。。。。。。end
	 * @author Administrator
	 *
	 */
	
}
