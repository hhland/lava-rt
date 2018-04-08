package lava.rt.instance;

import java.lang.reflect.InvocationTargetException;


public enum MethodInstance {

	
	close;
	
	
	public long invoke(Object...objects)  {
	    long re=0;
		for(Object object:objects) {
			try {
				object.getClass().getMethod(this.name()).invoke(object);
				re++;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
			}
		}
		long pred=re/objects.length;
		return pred;
	}
}
