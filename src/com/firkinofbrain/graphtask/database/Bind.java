package com.firkinofbrain.graphtask.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Bind {
	private long id;
	private long highItemId;
	private long lowItemId;
	
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public long getHighItemId(){
		return highItemId;
	}
	
	public void setHighItemId(long highItemId){
		this.highItemId = highItemId;
	}
	
	public long getLowItemId(){
		return lowItemId;
	}
	
	public void setLowItemId(long lowItemId){
		this.lowItemId = lowItemId;
	}
	
	public static void addBind(Map<Long, ArrayList<Long>> map, long parent, long child){
		
		if(!map.containsKey(parent)){
			ArrayList<Long> children = new ArrayList<Long>();
			children.add(child);
			map.put(parent, children);
		}else{
			ArrayList<Long> children = map.get(parent);
			children.add(child);
			map.put(parent, children);
		}
	}
}
