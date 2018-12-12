package lava.rt.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public   class LangCommon {

	public static <T> boolean isIn(T obj1, T[] objs) {
		for (T obj : objs) {
			if (isEquals(obj1, obj)) {
				return true;
			}
		}
		return false;
	}

	
	
	@SafeVarargs
	public static <T> boolean isEquals(T... objects) {
		for (int i = 0,j=1; j < objects.length; j++) {
			T objecti = objects[i],objectj=objects[j];
			if (objecti != null && !objecti.equals(objectj))
				return false;
			else if (objecti == null && objectj == null)
				continue;
		}
		return true;
	}
	
     public static  <T> List<T>  toList(T... ts){
		
		List<T> ret=new ArrayList<>();
		
		for(T t:ts) {
			ret.add( t);
		}
		return ret;
		
	}
     
     
     public static  <T> Set<T>  toSet(T... ts){
 		
 		Set<T> ret=new HashSet<>();
 		
 		for(T t:ts) {
 			ret.add( t);
 		}
 		return ret;
 		
 	}
	
   public static  <T> Map<String, T>  toMap(T... ts){
		
		Map<String, T> tm=new HashMap<>();
		
		for(T t:ts) {
			tm.put(t.toString(), t);
		}
		
		return tm;
		
	}
	
	
	public static  <T> Map<String, T>  toMap(Collection<T> ts){
		
		Map<String, T> tm=new HashMap<>();
		
		for(T t:ts) {
			tm.put(t.toString(), t);
		}
		
		return tm;
		
	}
	
	
	
}
