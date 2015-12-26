package com.nestoop.yelibar.akka.settings;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.ExtensionIdProvider;

import com.typesafe.config.Config;

/**
 * 读取文件的信息
 * @author nestoop root
 *
 */
public class SettingUtil   {
	
	
	public static class SettingsImpl implements Extension{
		//DB URL
		public  final String DB_URI;
		//read timeout
		public  final String TIMEOUT_READ;
		
		public  final String TIMEOUT_WRITE;
		
//		public  final Duration CIRCUIT_BREAKER_TIMEOUT;
		
		public SettingsImpl(Config config) {
			
//			config.atPath("resources/application.conf");
			//数据库负值
			DB_URI=config.getString("akka.actor.mailbox.mysql.uri");
			//读取
			TIMEOUT_READ=config.getString("akka.actor.mailbox.mysql.timeout.read");
			//写入
			TIMEOUT_WRITE=config.getString("akka.actor.mailbox.mysql.timeout.write");
			
			System.out.println("circuit-breaker.timeout--:"+config.getString("akka.actor.mailbox.mysql.circuit-breaker.timeout"));
			
//			CIRCUIT_BREAKER_TIMEOUT=Duration.create(config.getDuration("akka.actor.mailbox.mysql.circuit-breaker.timeout", TimeUnit.MILLISECONDS)+"");
		}
		
	}
	
	public static class Settings extends AbstractExtensionId<SettingsImpl> implements ExtensionIdProvider {
	    public final static Settings SettingsProvider = new Settings();
	 
	    public Settings lookup() {
	      return Settings.SettingsProvider;
	    }
	 
	    public SettingsImpl createExtension(ExtendedActorSystem system) {
	      return new SettingsImpl(system.settings().config());
	    }
	}

}
