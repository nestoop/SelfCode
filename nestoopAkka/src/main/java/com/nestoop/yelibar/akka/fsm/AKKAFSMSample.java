package com.nestoop.yelibar.akka.fsm;

import java.util.ArrayList;
import java.util.List;

import com.nestoop.yelibar.akka.fsm.FSMMessageBuncher.SetTarget;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * akka 的fsm模型
 * @author xbao
 *
 */
public class AKKAFSMSample {
	
	public static void main(String[] args) {
		
		ActorSystem system=ActorSystem.create("system");
		
		ActorRef fsm=system.actorOf(Props.create(MyFSM.class),"myfsm");
		
		fsm.tell(new SetTarget(fsm), fsm);
		
		
	}
	
	
	
	public abstract static class FSMBase extends UntypedActor{
		
	    /** 描述状态
		 * @author xbao
		 *
		 */
		protected enum State {
			IDLE, ACTIVE;
		}
		
		private State state = State.IDLE;
		private ActorRef target;
		private List<Object> queue;
		
		//初始化
		protected void init(ActorRef target){
			this.target=target;
			queue=new ArrayList<Object>();
		}
		
		//添加队列
		protected void enQueue(Object object){
			if (queue!=null) {
				queue.add(object);
			}
		}
		
		protected List<Object> drainQueue() {
			final List<Object> q = queue;
			if (q == null)
			throw new IllegalStateException("drainQueue(): not yet initialized");
			queue = new ArrayList<Object>();
			return q;
		} 
		/* *
		*是否给target 初始化
		*/
		protected boolean isInitialized() {
			return target != null;
		}
		//获取状态
		protected State getState() {
			return state;
		}
		//setstat
		protected void setState(State s){
			if(state !=s){
				transition(state, s);
				state=s;
			}
		}
		//获取目标引用
		protected ActorRef getTarget() {
			if (target == null)
			throw new IllegalStateException("getTarget(): not yet initialized");
			return target;
		} 
		/* *
		And finally the callbacks (only one in this example: react to state change)
		*/
		abstract protected void transition(State old, State next);

		@Override
		public void onReceive(Object message) throws Exception {
			
		}
	}
	
	/**
	 *定义自己的FMS
	 * 
	 */
	
	
	public static class MyFSM extends FSMBase{
		
		private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

		@Override
		public void onReceive(Object message) throws Exception {
			System.out.println("getState()=="+getState());
			System.out.println("message=="+message);
			if(getState() == State.IDLE){
				
				if(message instanceof SetTarget){
					init(((SetTarget) message).ref);
				}else{
					whenUnhandled(message);
				}
				
			}else if(getState() == State.ACTIVE){
				
				if(message == new FSMMessageBuncher().flush ){
					setState(State.IDLE);
				}else{
					whenUnhandled(message);
				}
			}
		}

		@Override
		protected void transition(State old, State next) {
			if(old ==State.ACTIVE){
				getTarget().tell(new FSMMessageBuncher.Batch(drainQueue()), getSelf());
			}
		}
		
		private void whenUnhandled(Object o) {
			
			if(o instanceof FSMMessageBuncher.Queue && isInitialized()){
				enQueue(o);
				setState(State.ACTIVE);
			}else{
				log.warning("received unknown message {} in state {}", o, getState());
			}
		}
	}
	
	

}
