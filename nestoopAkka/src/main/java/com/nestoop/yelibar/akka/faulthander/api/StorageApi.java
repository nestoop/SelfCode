package com.nestoop.yelibar.akka.faulthander.api;

import com.nestoop.yelibar.akka.faulthander.util.Entry;

public class StorageApi {
	
    public static class Store {
    	
        public final Entry entry;
   
        public Store(Entry entry) {
          this.entry = entry;
        }
   
        public String toString() {
          return String.format("%s(%s)", getClass().getSimpleName(), entry);
        }
    }

}
