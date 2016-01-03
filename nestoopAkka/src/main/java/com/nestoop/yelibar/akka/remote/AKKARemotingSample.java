package com.nestoop.yelibar.akka.remote;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.ExtensionIdProvider;
import akka.actor.UntypedActor;

import com.typesafe.config.Config;

/**
 * akkaԶ�̵��� ʵ��
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
	 * �Զ���config��ȡ��   ��������������������������������������start
	 * @author Administrator
	 *
	 */
	
	public static class RemoteSettingsImpl implements Extension{
		//����
		public  final String PROVIDER;
		//����
		public  final String HOSTNAME;
		//�˿�
		public  final String PORT;
		
		
		public RemoteSettingsImpl(Config config) {
			
//			config.atPath("resources/application.conf");
			//������
			PROVIDER=config.getString("akka.actor.provider");
			//����
			HOSTNAME=config.getString("akka.remote.netty.hostname");
			//д��
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
	 * �Զ���config��ȡ��   ��������������������������������������end
	 * @author Administrator
	 *
	 */
	
}
