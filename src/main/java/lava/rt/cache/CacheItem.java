package lava.rt.cache;

import java.io.Serializable;
import java.util.Date;

public class CacheItem<E> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final E item;
	
	 long timeoutAt;
	 
	public final String key;
	
    boolean enable=true;

	public CacheItem(E entity,String key,long timeoutMillsec) {
		super();
		this.item = entity;
		timeoutAt=System.currentTimeMillis()+timeoutMillsec;
		this.key=key;
	}
	
	public final E get() {
		return item;
	}
	
	public final boolean isTimeout() {
		boolean ret=System.currentTimeMillis()>timeoutAt;
		return ret;
	}
	
	
	public final void incrTimeout(long timeoutMillsec) {
		timeoutAt=System.currentTimeMillis()+timeoutMillsec;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
}
