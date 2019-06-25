package lava.rt.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import lava.rt.linq.Entity;

public abstract class CacheContainer  {

	public abstract void remove(CacheItem... items);
	
	public abstract <E> CacheItem<E> get(Class<E> cls, Object pk);
	
	public abstract <E> String put(E ret, Object pk,long timeoutMillsec);
	
	protected abstract Collection<CacheItem> coll();
	
	public String itemKey(Class cls, Object pk) {
		return cls.getName()+":"+pk;
	}
	
	
	
	public ClearItemThread getCleanItemThread() {
		ClearItemThread thread=new ClearItemThread(this);
		return thread;
	}
	
	
	
	protected class ClearItemThread extends Thread{
		
		CacheContainer cacheContainer;

		boolean pause=true,stop=false;
		
		
		public ClearItemThread(CacheContainer cacheContainer) {
			super();
			this.cacheContainer = cacheContainer;
		}




		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				if(stop)break;
				if(pause)continue;
				
				
				for(CacheItem cacheItem: cacheContainer.coll()) {
					
					if(cacheItem.isTimeout()) {
						cacheContainer.remove(cacheItem);
					}
					
				}
				
			}
		}
		
		
		
	}



	
	
}
