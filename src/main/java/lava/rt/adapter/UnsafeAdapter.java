package lava.rt.adapter;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeAdapter extends BaseAdapter<Unsafe>{

	public UnsafeAdapter() {
		super(getUnsafe());
		
		// TODO Auto-generated constructor stub
	}
	
	public UnsafeAdapter(Unsafe unsafe) {
		super(unsafe);
		// TODO Auto-generated constructor stub
		
	   
	}

	
	
	protected static Unsafe getUnsafe() {
    	Unsafe ret=null;
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
	        ret= (Unsafe) theUnsafe.get(null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return ret;
        
        
    }
	
	
	
	public   boolean compareAndSwapObject(Object obj,String fieldName, Object value) throws Exception {
	     Field field=obj.getClass().getDeclaredField(fieldName);
		 
	     return compareAndSwapObject(obj, field, value);
	}
	
	public   boolean compareAndSwapInt(Object obj,String fieldName, int value) throws Exception {
	     Field field=obj.getClass().getDeclaredField(fieldName);
		
	     
	     return compareAndSwapInt(obj, field, value);
	}
	
	public   boolean compareAndSwapLong(Object obj,String fieldName, long value) throws Exception {
	     Field field=obj.getClass().getDeclaredField(fieldName);
		 
	     
	     return compareAndSwapLong(obj, field, value);
	}
	
	
	public   boolean compareAndSwapObject(Object obj,Field field, Object value) throws Exception {
	     
		 long fieldOffset=_this.objectFieldOffset(field);
	     
	     return _this.compareAndSwapObject(obj, fieldOffset, field.get(obj), value);
	}
	
	public   boolean compareAndSwapInt(Object obj,Field field, int value) throws Exception {
	    
		 long fieldOffset=_this.objectFieldOffset(field);
	     
	     return _this.compareAndSwapInt(obj, fieldOffset, (int)field.get(obj), value);
	}
	
	public   boolean compareAndSwapLong(Object obj,Field field, long value) throws Exception {
	     
		 long fieldOffset=_this.objectFieldOffset(field);
	     
	     return _this.compareAndSwapLong(obj, fieldOffset,(long)field.get(obj), value);
	}

	
	
	
}
