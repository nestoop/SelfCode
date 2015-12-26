package com.nestoop.yelibar.akka.props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * 
 * @author xbao
 *
 */
public class PropsUtil{
	
	// make a Config with just your special setting
	final Config myConfig =ConfigFactory.parseString("something=somethingElse");
			// load the normal config stack (system props,
			// then application.conf, then reference.conf)
	final Config regularConfig =ConfigFactory.load();
			// override regular stack with myConfig
	final Config combined = myConfig.withFallback(regularConfig);
			// put the result in between the overrides
			// (system props) and defaults again
	final Config completeConfig =ConfigFactory.load(combined);
	
	final ActorSystem system = ActorSystem.create("SystemActor",completeConfig);
	
	final ActorRef myActor = system.actorOf(Props.create(SystemActor.class), "myactor");
	
	public static class SystemActor extends AbstractActor{
		
	} 
	public static class FirstActor extends AbstractActor{
		final ActorRef child = context().actorOf(Props.create(SystemActor.class), "myChild");
		// plus some behavior ...
	} 
	
	

}
