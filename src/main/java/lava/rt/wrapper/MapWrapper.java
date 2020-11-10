package lava.rt.wrapper;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;


public class MapWrapper<K,V> extends BaseWrapper<Map<K,V>>{

	public MapWrapper(Map<K,V> self) {
		super(self);
		// TODO Auto-generated constructor stub
	}

	
	
	
	public int removeIf(Function<Entry<K, V>,Boolean> handler) {
		
		int ret=0;
		
		Set<K> removeKeys=new HashSet<>();
		
		for(Entry<K, V> entry:self.entrySet()) {
			if(handler.apply(entry))removeKeys.add(entry.getKey());
		}
		
		for(K removeKey :removeKeys) {
			V val=self.remove(removeKey);
			if(val!=null)ret++;
		}
		
		return ret;
	}
}
