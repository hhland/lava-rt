package lava.rt.wrapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import lava.rt.common.ReflectCommon;
import lava.rt.linq.sql.Table;
import sun.misc.Unsafe;

public class UnsafeWrapper extends BaseWrapper<Unsafe>{

    protected  static UnsafeWrapper instance;
    
    
    protected static final Map<String,Long> fieldOffsetMap=new HashMap<>();
  
	
	protected UnsafeWrapper(Unsafe unsafe) {
		super(unsafe);
		// TODO Auto-generated constructor stub
	}
	
	
	public static UnsafeWrapper getInstance(){
		
		if(instance==null){
			try {
				Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
				theUnsafe.setAccessible(true);
				Unsafe us= (Unsafe) theUnsafe.get(null);
				instance=new UnsafeWrapper(us);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
		
	}

	
	public  Map<String,Long> theDeclaredFieldOffsetMap(Class cls){
    	Map<String,Long> ret=new HashMap<String,Long>();
    	
    	
    	for(Entry<String, Field> ent:ReflectCommon.getTheDeclaredFieldMap(cls).entrySet()) {
    		ret.put(ent.getKey(), _this.objectFieldOffset(ent.getValue()));
    	}
    	
    	return ret;
    }
	
	public  Map<String,Long> allDeclaredFieldOffsetMap(Class cls){
    	Map<String,Long> ret=new HashMap<String,Long>();
    	
    	
    	for(Entry<String, Field> ent:ReflectCommon.getAllDeclaredFieldMap(cls).entrySet()) {
    		ret.put(ent.getKey(), _this.objectFieldOffset(ent.getValue()));
    	}
    	
    	return ret;
    }


	public Long objectFieldOffset(Field field) {
		// TODO Auto-generated method stub
		
		return _this.objectFieldOffset(field);
	}


	public <T> T getObject(Object object, Long fieldOffset) {
		// TODO Auto-generated method stub
		return (T)_this.getObject(object, fieldOffset);
	}


	public void putObject(Object object, Long offset, Object value) {
		// TODO Auto-generated method stub
	
		_this.putObject(object,offset,value);
	}


	public <E> E allocateInstance(Class<E> entryClass) throws InstantiationException {
		// TODO Auto-generated method stub
		return (E)_this.allocateInstance(entryClass);
	    
	}
	
}
