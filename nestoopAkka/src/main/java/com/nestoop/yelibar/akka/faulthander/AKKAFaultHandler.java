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
 * akka 对于错误处理
 * @author Administrator
 *
 */
public class AKKAFaultHandler {
	
	public static void main(String[] args) {
		//手动配置 使用配置文件方式生成ActorSystem
	    Config config = ConfigFactory.parseString("akka.loglevel = DEBUG \n" + "akka.actor.debug.lifecycle = on");
	    ActorSystem system = ActorSystem.create("FaultToleranceSample", config);
	    ActorRef worker = system.actorOf(Props.create(Worker.class), "worker");
	    ActorRef listener = system.actorOf(Props.create(Listener.class), "listener");
	    worker.tell(WokerApi.START, listener);
	}
	
	
	/**
	 * 
	 * 创建Listener,启动和shut down worker
	 * @author xbao
	 *
	 */
	
	public static class Listener extends UntypedActor{
		
		final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
		
		/**
		 * 15s 后接收不到的消息，表示worker就是无效的。
		 */
		@Override
		public void preStart() throws Exception {
			getContext().setReceiveTimeout(Duration.apply(15,TimeUnit.SECONDS));
		};

		@Override
		public void onReceive(Object message) throws Exception {
			 logger.debug("Listener received message {}", message);
			 //接收progress
			 if(message instanceof WokerApi.Progress){
				 WokerApi.Progress progress=(WokerApi.Progress) message;
				 logger.info("Current Progress :{} %s",progress.percent);
				 if(progress.percent>=100.0){
					 logger.info("That is all ,shutting down!");
					 getContext().system().shutdown();
				 }else if(message == getContext().receiveTimeout()){
					 //15秒之后没有消息，shutdown
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
	 * 创建worker   ,当接收到listener 发送的启动指令后，启动当前进程。
	 * 
	 */
	
	public  static class Worker extends UntypedActor{
		//日志
		final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
		//超时设置
		final Timeout askTimeout = new Timeout(Duration.create(5,TimeUnit.SECONDS));
		//监听
		 ActorRef progressListener;
		//计数服务
		final ActorRef counterService=getContext().system().actorOf(Props.create(CounterService.class), "counter");
		
		//定义总记录数
		final long totalCount=51L;
		
		// Stop the CounterService child if it throws ServiceUnavailable
	    private static SupervisorStrategy strategy = new OneForOneStrategy(-1, Duration.Inf(),
	        new Function<Throwable, Directive>() {
	          @Override
	          public Directive apply(Throwable t) {
	            if (t instanceof ServiceUnavailable) {
	            	//stop 方法
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
				//附上ActorRef
				progressListener=getSelf();
				//从现在开始，每隔一秒发送一个"do"message
				getContext().system().scheduler().schedule(Duration.Zero(),Duration.create(1,TimeUnit.SECONDS),getSelf(),WokerApi.DO,getContext().dispatcher(),null);
			}else if(message.equals(WokerApi.DO)){
				//处理计数逻辑，先空着
				counterService.tell(new Increment(1), getSelf());
				counterService.tell(new Increment(1), getSelf());
				counterService.tell(new Increment(1), getSelf());
				//Actor extends
				ExecutionContext executionContext=getContext().system().dispatcher();
				//  Send current progress to the initial sender 当前程序向初始化的发送者发送消息
				//future 执行过程成返回值  这个事jdk8新引进的lamda 本人用java 写出
				Future<Object> future=Patterns.ask(counterService, CounterServiceApi.GetCurrentCount, askTimeout);
				
				future.mapTo(classTag(CounterServiceApi.CurrentCount.class)).map(new Mapper<CounterServiceApi.CurrentCount,WokerApi.Progress>() {

					@Override
					public Progress apply(CurrentCount parameter) {
						return new WokerApi.Progress(100.0*parameter.count/totalCount);
					}
				 	
				}, executionContext);
				//pipe 模式
				Patterns.pipe(future, executionContext).to(progressListener);
				
				
			}else{
				unhandled(message);
			}
			
		}

		
	}
	
	

	
	/**
	 * 定义counterservice
	 * 
	 */
	
	public static class CounterService extends UntypedActor{
		
		final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
		
		public static final Object Reconnect="Reconnect";
		
		//key 是akka的路径
		 final String key=getSelf().path().name();
		//counterservice 的组件之一 ：Storage(存储组件)
		 ActorRef storage;
		//counterservice 的组件之一 ：Counter(计算组件)
		 ActorRef counter;
		
	    final List<SenderMsgPair> backlog = new ArrayList<SenderMsgPair>();
	    
	    final int MAX_BACKLOG = 10000;
	    //这个是什么策略， 使用的是one to one  在3秒重启间隔5s之内，否则停止
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
	    	//创建存储 ActorRef 
	    	storage=getContext().watch(getContext().actorOf(Props.create(Storage.class),"storage"));
	    	
	    	//counter想counterService
	    	if(counter != null){
	    		counter.tell(new CounterApi.UseStorage(storage),getSelf());
	    	}
	    	//storage 发送消息，也向counterservice 发送服务
	    	
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
				//counter 为空，就要创建counter 的ActorRef
				final long value=((Entry)message).value;
				//创建counter的ActorRef,且是有参数构造器
				counter=getContext().actorOf(Props.create(Counter.class, new Creator<Counter>() {
					@Override
					public Counter create() throws Exception {
						return new Counter(key,value);
					}
				}));
				//告诉storage 当前的存储
				counter.tell(new CounterApi.UseStorage(storage), getSelf());
				//向counter发送bloklog消息
				for(SenderMsgPair pair:backlog){
					counter.tell(pair.message, pair.sender);
				}
				//清除缓冲区
				backlog.clear();
			}else if(message instanceof Increment){//启动增加时，需要告诉counter,向counter发送的消息可以先存到backlog
				forwardOrPlaceInBacklog(message);
			}else  if(message.equals(CounterServiceApi.GetCurrentCount)){
				forwardOrPlaceInBacklog(message);
			}else if(message instanceof Terminated){//接收的消息是终止Actor
				storage=null;
				counter.tell(new CounterApi.UseStorage(null), getSelf());
				//在10之后开始发送重新链接的消息
				getContext().system().scheduler().scheduleOnce$default$5(Duration.create(10,TimeUnit.SECONDS),getSelf(),Reconnect);
			}else if(message.equals(Reconnect)){
				//初始化storage
				initStorage();
			}else{
				unhandled(message);
			}
			
		}
		
		/**
		 * 在初始化之前，派个代表告诉counter,且把消息放到backlog中，之后发送给counter
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
			}else if(message instanceof Increment){//增长计数,count增加
				count+=((Increment)message).n;
				
			}else if(message.equals(CounterServiceApi.GetCurrentCount)){//counter自己告知自己
				getSender().tell(new CounterServiceApi.CurrentCount(key,count),getSelf());
			}else{
				unhandled(message);
			}
			
		}
		//存储计数
	    void storeCount() {
	        // Delegate dangerous work, to protect our valuable state.
	        // We can continue without storage.
	        if (storage != null) {
	          storage.tell(new StorageApi.Store(new Entry(key, count)), getSelf());
	        }
	   }
	}
	
	/**
	 * 存储计数的Actor
	 * 
	 */
	public  static class Storage extends UntypedActor{
	    final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	    
	    final DummyDB db = DummyDB.instance;
	 

		@Override
		public void onReceive(Object message) throws Exception {
			
			if(message instanceof StorageApi.Store){//存储消息
				
				StorageApi.Store store=(StorageApi.Store)message;
				//存储内存
				db.save(store.entry.key, store.entry.value);
				
			}else if(message instanceof Get){
				
				Get get=(Get)message;
				
				//从缓存中获取value
				Long  value=db.load(get.key);
				//需要添加value 的判断
				getSender().tell(new Entry(get.key,value == null?Long.valueOf(0L):value),getSelf());
			}else {
				unhandled(message);
			}
		}
		
		
	}
	/**
	 * 写一个缓存框架
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
