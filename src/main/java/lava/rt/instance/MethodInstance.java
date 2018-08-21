package lava.rt.instance;

import java.lang.reflect.InvocationTargetException;


public enum MethodInstance {

	
	close,finalize;
	
	
	public float invoke(Object...objects)  {
	    float re=0;
		for(Object object:objects) {
			try {
				object.getClass().getMethod(this.name()).invoke(object);
				re++;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
			}
		}
		float pred=re/objects.length;
		return pred;
	}
}
