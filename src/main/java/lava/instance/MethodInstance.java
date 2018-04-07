package lava.instance;

import java.lang.reflect.InvocationTargetException;


public enum MethodInstance {

	
	close;
	
	
	public void invoke(Object...objects) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
	
		for(Object object:objects) {
			object.getClass().getMethod(this.name()).invoke(object);
		}
	}
}
