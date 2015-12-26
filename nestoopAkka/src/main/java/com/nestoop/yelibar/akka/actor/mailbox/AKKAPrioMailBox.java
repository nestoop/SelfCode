package com.nestoop.yelibar.akka.actor.mailbox;




import java.util.Comparator;

import akka.dispatch.Envelope;
import akka.dispatch.UnboundedPriorityMailbox;

/**
 * akkaµƒ” œ‰
 * @author xbao
 *
 */
public class AKKAPrioMailBox extends UnboundedPriorityMailbox{
	
	public static final int initCapacity=270*10000;

	public AKKAPrioMailBox(Comparator<Envelope> cmp, int initialCapacity) {
		super(cmp, initialCapacity);
		
	}
	
	public static class MailBoxComparator implements Comparator<Envelope>{

		@Override
		public int compare(Envelope o1, Envelope o2) {
			// TODO Auto-generated method stub
			return compare(o1, o2);
		}
		
		
	}
	
	
	

	
	
}
