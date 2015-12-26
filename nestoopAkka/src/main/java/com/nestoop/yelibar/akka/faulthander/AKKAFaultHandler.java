package com.nestoop.yelibar.akka.faulthander;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.nestoop.yelibar.akka.faulthander.api.CounterApi;
import com.nestoop.yelibar.akka.faulthander.api.CounterServiceApi;
import com.nestoop.yelibar.akka.faulthander.api.CounterServiceApi.CurrentCount;
import com.nestoop.yelibar.akka.faulthander.api.StorageApi;
import com.nestoop.yelibar.akka.faulthander.api.WokerApi;
import com.nestoop.yelibar.akka.faulthander.api.SenderMsgPair;
import com.nestoop.yelibar.akka.faulthander.api.WokerApi.Progress;
import com.nestoop.yelibar.akka.faulthander.exception.ServiceUnavailable;
import com.nestoop.yelibar.akka.faulthander.exception.StorageException;
import com.nestoop.yelibar.akka.faulthander.util.Entry;
import com.nestoop.yelibar.akka.faulthander.util.Get;
import com.nestoop.yelibar.akka.faulthander.util.Increment;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.actor.SupervisorStrategy.Directive;
import akka.dispatch.Mapper;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import akka.japi.Function;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import static akka.japi.Util.classTag;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;
import static akka.actor.SupervisorStrategy.escalate;
/**
 * akka ���ڴ�����
 * @author Administrator
 *
 */
public class AKKAFaultHandler {
	
	public static void main(String[] args) {
		//�ֶ����� ʹ�������ļ���ʽ����ActorSystem
	    Config config = ConfigFactory.parseString("akka.loglevel = DEBUG \n" + "akka.actor.debug.lifecycle = on");
	    ActorSystem system = ActorSystem.create("FaultToleranceSample", config);
	    ActorRef worker = system.actorOf(Props.create(Worker.class), "worker");
	    ActorRef listener = system.actorOf(Props.create(Listener.class), "listener");
	    worker.tell(WokerApi.START, listener);
	}
	
	
	/**
	 * 
	 * ����Listener,������shut down worker
	 * @author xbao
	 *
	 */
	
	public static class Listener extends UntypedActor{
		
		final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
		
		/**
		 * 15s ����ղ�������Ϣ����ʾworker������Ч�ġ�
		 */
		@Override
		public void preStart() throws Exception {
			getContext().setReceiveTimeout(Duration.apply(15,TimeUnit.SECONDS));
		};

		@Override
		public void onReceive(Object message) throws Exception {
			 logger.debug("Listener received message {}", message);
			 //����progress
			 if(message instanceof WokerApi.Progress){
				 WokerApi.Progress progress=(WokerApi.Progress) message;
				 logger.info("Current Progress :{} %s",progress.percent);
				 if(progress.percent>=100.0){
					 logger.info("That is all ,shutting down!");
					 getContext().system().shutdown();
				 }else if(message == getContext().receiveTimeout()){
					 //15��֮��û����Ϣ��shutdown
					 logger.error("Shutting down due to unavailable service");
					 getContext().system().shutdown();
				 }else{
					 unhandled(message);
				 }
				 
			 }
		}
		
	}
	
	
	
	/**
	 * 
	 * ����worker   ,�����յ�listener ���͵�����ָ���������ǰ���̡�
	 * 
	 */
	
	public  static class Worker extends UntypedActor{
		//��־
		final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
		//��ʱ����
		final Timeout askTimeout = new Timeout(Duration.create(5,TimeUnit.SECONDS));
		//����
		 ActorRef progressListener;
		//��������
		final ActorRef counterService=getContext().system().actorOf(Props.create(CounterService.class), "counter");
		
		//�����ܼ�¼��
		final long totalCount=51L;
		
		// Stop the CounterService child if it throws ServiceUnavailable
	    private static SupervisorStrategy strategy = new OneForOneStrategy(-1, Duration.Inf(),
	        new Function<Throwable, Directive>() {
	          @Override
	          public Directive apply(Throwable t) {
	            if (t instanceof ServiceUnavailable) {
	            	//stop ����
	              return stop();
	            } else {
	              return escalate();
	            }
	          }
	        });
	 
	    @Override
	    public SupervisorStrategy supervisorStrategy() {
	      return strategy;
	    }
		
		@Override
		public void onReceive(Object message) throws Exception {
			logger.debug("Worker received message {}", message);
			if(message.equals(WokerApi.START) && progressListener== null){
				//����ActorRef
				progressListener=getSelf();
				//�����ڿ�ʼ��ÿ��һ�뷢��һ��"do"message
				getContext().system().scheduler().schedule(Duration.Zero(),Duration.create(1,TimeUnit.SECONDS),getSelf(),WokerApi.DO,getContext().dispatcher(),null);
			}else if(message.equals(WokerApi.DO)){
				//��������߼����ȿ���
				counterService.tell(new Increment(1), getSelf());
				counterService.tell(new Increment(1), getSelf());
				counterService.tell(new Increment(1), getSelf());
				//Actor extends
				ExecutionContext executionContext=getContext().system().dispatcher();
				//  Send current progress to the initial sender ��ǰ�������ʼ���ķ����߷�����Ϣ
				//future ִ�й��̳ɷ���ֵ  �����jdk8��������lamda ������java д��
				Future<Object> future=Patterns.ask(counterService, CounterServiceApi.GetCurrentCount, askTimeout);
				
				future.mapTo(classTag(CounterServiceApi.CurrentCount.class)).map(new Mapper<CounterServiceApi.CurrentCount,WokerApi.Progress>() {

					@Override
					public Progress apply(CurrentCount parameter) {
						return new WokerApi.Progress(100.0*parameter.count/totalCount);
					}
				 	
				}, executionContext);
				//pipe ģʽ
				Patterns.pipe(future, executionContext).to(progressListener);
				
				
			}else{
				unhandled(message);
			}
			
		}

		
	}
	
	

	
	/**
	 * ����counterservice
	 * 
	 */
	
	public static class CounterService extends UntypedActor{
		
		final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
		
		public static final Object Reconnect="Reconnect";
		
		//key ��akka��·��
		 final String key=getSelf().path().name();
		//counterservice �����֮һ ��Storage(�洢���)
		 ActorRef storage;
		//counterservice �����֮һ ��Counter(�������)
		 ActorRef counter;
		
	    final List<SenderMsgPair> backlog = new ArrayList<SenderMsgPair>();
	    
	    final int MAX_BACKLOG = 10000;
	    //�����ʲô���ԣ� ʹ�õ���one to one  ��3���������5s֮�ڣ�����ֹͣ
	    private static SupervisorStrategy strategy = new OneForOneStrategy(3,Duration.apply(5,TimeUnit.SECONDS),new Function<Throwable, SupervisorStrategy.Directive>() {
			
			@Override
			public Directive apply(Throwable t) throws Exception {
				if(t instanceof StorageException){
					return restart();
				}
				return escalate();
			}
		});
	    
	    
	    void initStorage(){
	    	//�����洢 ActorRef 
	    	storage=getContext().watch(getContext().actorOf(Props.create(Storage.class),"storage"));
	    	
	    	//counter��counterService
	    	if(counter != null){
	    		counter.tell(new CounterApi.UseStorage(storage),getSelf());
	    	}
	    	//storage ������Ϣ��Ҳ��counterservice ���ͷ���
	    	
	    	storage.tell(new Get(key),getSelf());
	    }
	    
	    
	    

		@Override
		public SupervisorStrategy supervisorStrategy() {
			
			return strategy;
		}
		

		@Override
		public void preStart() throws Exception {
			initStorage();
		}

		@Override
		public void onReceive(Object message) throws Exception {
			log.debug("counterService received message {}", message);
			if(message instanceof Entry && ((Entry) message).key.equals(key) && counter == null){
				//counter Ϊ�գ���Ҫ����counter ��ActorRef
				final long value=((Entry)message).value;
				//����counter��ActorRef,�����в���������
				counter=getContext().actorOf(Props.create(Counter.class, new Creator<Counter>() {
					@Override
					public Counter create() throws Exception {
						return new Counter(key,value);
					}
				}));
				//����storage ��ǰ�Ĵ洢
				counter.tell(new CounterApi.UseStorage(storage), getSelf());
				//��counter����bloklog��Ϣ
				for(SenderMsgPair pair:backlog){
					counter.tell(pair.message, pair.sender);
				}
				//���������
				backlog.clear();
			}else if(message instanceof Increment){//��������ʱ����Ҫ����counter,��counter���͵���Ϣ�����ȴ浽backlog
				forwardOrPlaceInBacklog(message);
			}else  if(message.equals(CounterServiceApi.GetCurrentCount)){
				forwardOrPlaceInBacklog(message);
			}else if(message instanceof Terminated){//���յ���Ϣ����ֹActor
				storage=null;
				counter.tell(new CounterApi.UseStorage(null), getSelf());
				//��10֮��ʼ�����������ӵ���Ϣ
				getContext().system().scheduler().scheduleOnce$default$5(Duration.create(10,TimeUnit.SECONDS),getSelf(),Reconnect);
			}else if(message.equals(Reconnect)){
				//��ʼ��storage
				initStorage();
			}else{
				unhandled(message);
			}
			
		}
		
		/**
		 * �ڳ�ʼ��֮ǰ���ɸ��������counter,�Ұ���Ϣ�ŵ�backlog�У�֮���͸�counter
		 * @param message
		 */
		public void forwardOrPlaceInBacklog(Object message){
			
			if(counter == null){
				if(backlog.size()>=MAX_BACKLOG){
					throw new ServiceUnavailable("CounterService not available, lack of initial value");
				}
				backlog.add(new SenderMsgPair(getSender(), message));
			}else{
				counter.forward(message, getContext());
			}
		}
	}
	
	
	
	public static class Counter extends UntypedActor{
		
	    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	    final String key;
	    long count;
	    ActorRef storage;
	    
	    public Counter(String key,long initValue){
	    	this.key=key;
	    	this.count=initValue;
	    }
	    
	    
		@Override
		public void onReceive(Object message) throws Exception {
			
			if(message instanceof CounterApi.UseStorage){
				storage=((CounterApi.UseStorage)message).storage;
				storeCount();
			}else if(message instanceof Increment){//��������,count����
				count+=((Increment)message).n;
				
			}else if(message.equals(CounterServiceApi.GetCurrentCount)){//counter�Լ���֪�Լ�
				getSender().tell(new CounterServiceApi.CurrentCount(key,count),getSelf());
			}else{
				unhandled(message);
			}
			
		}
		//�洢����
	    void storeCount() {
	        // Delegate dangerous work, to protect our valuable state.
	        // We can continue without storage.
	        if (storage != null) {
	          storage.tell(new StorageApi.Store(new Entry(key, count)), getSelf());
	        }
	   }
	}
	
	/**
	 * �洢������Actor
	 * 
	 */
	public  static class Storage extends UntypedActor{
	    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	    
	    final DummyDB db = DummyDB.instance;
	 

		@Override
		public void onReceive(Object message) throws Exception {
			
			if(message instanceof StorageApi.Store){//�洢��Ϣ
				
				StorageApi.Store store=(StorageApi.Store)message;
				//�洢�ڴ�
				db.save(store.entry.key, store.entry.value);
				
			}else if(message instanceof Get){
				
				Get get=(Get)message;
				
				//�ӻ����л�ȡvalue
				Long  value=db.load(get.key);
				//��Ҫ���value ���ж�
				getSender().tell(new Entry(get.key,value == null?Long.valueOf(0L):value),getSelf());
			}else {
				unhandled(message);
			}
		}
		
		
	}
	/**
	 * дһ��������
	 * @author Administrator
	 *
	 */
	public static class DummyDB {
	    public static final DummyDB instance = new DummyDB();
	    private final Map<String, Long> db = new HashMap<String, Long>();
	 
	    private DummyDB() {
	    }
	 
	    public synchronized void save(String key, Long value) throws StorageException {
	      if (11 <= value && value <= 14)
	        throw new StorageException("Simulated store failure " + value);
	      db.put(key, value);
	    }
	 
	    public synchronized Long load(String key) throws StorageException {
	      return db.get(key);
	    }
	  }
}
