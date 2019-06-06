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
	
}
