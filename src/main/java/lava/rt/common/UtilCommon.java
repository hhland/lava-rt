package lava.rt.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface UtilCommon {

	
	
	
	public static <K,V> Map<K,V> toMap(Collection<V> sources,Function<V,  K> keyFun){
		Map<K,V> ret=new HashMap<>();
		for(V source :sources) {
			ret.put(keyFun.apply(source), source);
		}
		return ret;
	}
	
}
