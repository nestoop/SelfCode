package com.nestoop.yelibar.akka.remote;

import java.io.Serializable;

public class MathOperationSample {
	
	public interface MathOperation extends Serializable{
		
	}
	public interface MathOperationResult extends Serializable{
		
	}
	
	public static class Add implements MathOperation{

		private static final long serialVersionUID = 1L;
		
		private final int n1;
		
		private final int n2;
		
		public Add(int n1,int n2){
			this.n1=n1;
			this.n2=n2;
		}
		
		public int getN1(){
			 return n1;
		}
		
		public int getN2(){
			return n2;
		}
		
		
	}
	public static class AddResult implements MathOperationResult{

		private static final long serialVersionUID = 1L;
		
		private final int n1;
		
		private final int n2;
		
		private final int result;
		
		public AddResult(int n1,int n2,int result){
			this.n1=n1;
			this.n2=n2;
			this.result=result;
		}
		
		public int getN1(){
			 return n1;
		}
		
		public int getN2(){
			return n2;
		}
		
		public int getResult(){
			return result;
		}
		
		
		
	}
	
	
	
	
	

}
