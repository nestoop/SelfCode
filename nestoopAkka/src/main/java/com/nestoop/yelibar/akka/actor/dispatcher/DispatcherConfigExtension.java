package com.nestoop.yelibar.akka.actor.dispatcher;


import com.typesafe.config.Config;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.Extension;
import akka.actor.ExtensionIdProvider;
import akka.event.slf4j.Logger;

public class DispatcherConfigExtension implements Extension {
	
	org.slf4j.Logger logger=Logger.root();
	 
	
	public final Config config;
	
	
	public DispatcherConfigExtension(Config config){
		config.atPath("resources/dispatcher.conf");
		this.config=config;
		
	}
	
	public synchronized  String Load(String key){
		
		String value=config.getString(key);
		
		if(value== null || "".equals(value)){
			logger.info("Config key is Empty,{}",config);
		}else{
			logger.info("Config (key,value) is (%s,%s)",key,value);
		}
		
		return value;
	}
	
	
	
	
	
	public static class ConfigSetting extends AbstractExtensionId<DispatcherConfigExtension> implements ExtensionIdProvider {
		
	    public final static ConfigSetting SettingsProvider = new ConfigSetting();
	 
	    public ConfigSetting lookup() {
	      return ConfigSetting.SettingsProvider;
	    }
	 
	    public DispatcherConfigExtension createExtension(ExtendedActorSystem system) {
	      return new DispatcherConfigExtension(system.settings().config());
	    }
	    
	    
	}


}
