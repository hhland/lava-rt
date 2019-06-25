package lava.rt.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryCacheContainer  extends CacheContainer{

	
	Map<String, CacheItem> cacheItemMap;
	
	public MemoryCacheContainer() {
		super();
		cacheItemMap=new HashMap<>();
	}
	
	
	public MemoryCacheContainer(Map<String, CacheItem> cacheItemMap) {
		super();
		this.cacheItemMap = cacheItemMap;
	}

	@Override
	public <E> CacheItem<E> get(Class<E> cls, Object pk) {
		// TODO Auto-generated method stub
		String key=itemKey(cls, pk);
		CacheItem<E> ret=cacheItemMap.get(key);
		return ret;
	}

	@Override
	public <E> String put(E ret, Object pk,long timeoutMillsec) {
		// TODO Auto-generated method stub
		String key=itemKey(ret.getClass(), pk);
		cacheItemMap.put(key, new CacheItem<E>(ret,key, timeoutMillsec));
		return key;
	}


	@Override
	public void remove(CacheItem... items) {
		// TODO Auto-generated method stub
		for(CacheItem item:items) {
			cacheItemMap.remove(item);
		}
		
	}


	@Override
	protected Collection<CacheItem> coll() {
		// TODO Auto-generated method stub
		return cacheItemMap.values();
	}

}
