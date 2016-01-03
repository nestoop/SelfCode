package com.nestoop.yelibar.akka.cluster;

import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClusterApp {
	public static void main(String[] args) {
		if (args.length == 0)
			startup(new String[] { "2551", "2552", "0" });
		else
			startup(args);
	}

	public static void startup(String[] ports) {
		for (String port : ports) {
			//∂¡»°≈‰÷√Œƒº˛ cluster.conf
			Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.load("cluster.conf"));

			// Create an Akka system
			ActorSystem system = ActorSystem.create("ClusterSystem", config);

			// Create an actor that handles cluster domain events
			system.actorOf(Props.create(SampleAKKAClusterListener.class),"clusterListener");

		}
	}
}
