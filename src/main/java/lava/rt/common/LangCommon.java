package lava.rt.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public  final  class LangCommon {

	

	
	
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
		
    	 return Stream.of(ts).collect(Collectors.toList());
		
	}
     
     
     public static  <T> Set<T>  toSet(T... ts){
 		
 		
 		return Stream.of(ts).collect(Collectors.toSet());
 		
 		
 	}
	
   public static  <T> Map<String, T>  toMap(T... ts){
	   return Stream.of(ts).collect(Collectors.toMap(t->t.toString(),t->t ));
		
	}
	
	
	public static  <T> Map<String, T>  toMap(Collection<T> ts){
		
		return ts.stream().collect(Collectors.toMap(t->t.toString(),t->t ));
		
	}
	
	
	
}
