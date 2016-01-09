package com.nestoop.yelibar.akka.cluster.traffic;

public class TrafficMessageApp {
	
	public static void main(String[] args) {
		  TrafficMessageBackendMain.main(new String[] { "2551" });
		  TrafficMessageBackendMain.main(new String[] { "2552" });
		  TrafficMessageBackendMain.main(new String[0]);
		  
		  TrafficMessageFrontendMain.main(new String[0]);
	}

}
