package com.nestoop.yelibar.akka.remote;

import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.Random;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.ConfigFactory;

public class CreationApplication {

  public static void main(String[] args) {
    if (args.length == 0 || args[0].equals("CalculatorWorker"))
      startRemoteWorkerSystem();
    if (args.length == 0 || args[0].equals("Creation"))
      startRemoteCreationSystem();
  }

  public static void startRemoteWorkerSystem() {
	  //����calculator.conf
    ActorSystem.create("CalculatorWorkerSystem",ConfigFactory.load(("calculator")));
    System.out.println("Started CalculatorWorkerSystem");
  }

  public static void startRemoteCreationSystem() {
	//����remotecreation.conf
    final ActorSystem system = ActorSystem.create("CreationSystem",ConfigFactory.load("remotecreation"));
    
    final ActorRef actor = system.actorOf(Props.create(CreationActor.class),"creationActor");
    
    System.out.println("Started CreationSystem");
    final Random r = new Random();
    //ÿ��1�뷢����Ӳ����¼�
    system.scheduler().schedule(Duration.create(1, SECONDS),
        Duration.create(1, SECONDS), new Runnable() {
          @Override
          public void run() {
              actor.tell(new MathOperationSample.Add(r.nextInt(100), r.nextInt(100)), null);
          }
        }, system.dispatcher());
  }
}
