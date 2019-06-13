package lava.rt.base;

import java.util.ArrayList;
import java.util.Random;

public abstract class PoolList<E> extends ArrayList<E>{

	


	private Integer index=0;
	
	
    private final Random random=new Random();
	

	public PoolList(int initialCapacity) throws Exception {
		super();
		// TODO Auto-generated constructor stub
		for(int i=0;i<initialCapacity;i++) {
			this.add(newSingle(i));
		}
		
	}
	
	
	public E getNext() {
		synchronized(index) {
			return this.get(index++%this.size());
		}
	}
	
	
	public E getRandom() {
		synchronized(random) {
			return this.get(random.nextInt(this.size()));
		}
	}
	
	public abstract E newSingle(int i) throws Exception;

}