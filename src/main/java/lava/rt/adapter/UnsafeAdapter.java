package lava.rt.adapter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import lava.rt.common.ReflectCommon;
import sun.misc.Unsafe;

public class UnsafeAdapter extends BaseAdapter<Unsafe>{

	
	
	public UnsafeAdapter(Unsafe unsafe) {
		super(unsafe);
		// TODO Auto-generated constructor stub
		
	   
	}

	
	public  Map<String,Long> theDeclaredFieldOffsetMap(Class cls){
    	Map<String,Long> ret=new HashMap<String,Long>();
    	
    	
    	for(Entry<String, Field> ent:ReflectCommon.theDeclaredFieldMap(cls).entrySet()) {
    		ret.put(ent.getKey(), _this.objectFieldOffset(ent.getValue()));
    	}
    	
    	return ret;
    }
	
	public  Map<String,Long> allDeclaredFieldOffsetMap(Class cls){
    	Map<String,Long> ret=new HashMap<String,Long>();
    	
    	
    	for(Entry<String, Field> ent:ReflectCommon.allDeclaredFieldMap(cls).entrySet()) {
    		ret.put(ent.getKey(), _this.objectFieldOffset(ent.getValue()));
    	}
    	
    	return ret;
    }
	
}
