package lava.rt.base;

import java.util.Date;

public class CacheBox<E extends Cloneable> {

	final E entity;
	
	 long timeoutAt;
	
    boolean enable=true;

	public CacheBox(E entity,long timeoutMillsec) {
		super();
		this.entity = entity;
		timeoutAt=System.currentTimeMillis()+timeoutMillsec;
	}
	
	public final E getEntity() {
		return entity;
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
