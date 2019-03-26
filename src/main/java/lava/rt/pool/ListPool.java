package lava.rt.pool;

import java.util.ArrayList;
import java.util.Random;

public abstract class ListPool<E> extends ArrayList<E>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3812995734231209299L;


	private Integer index=0;
	
	
    private Random random=new Random();
	

	public ListPool(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
		for(E e :this) {
			e=newSingle();
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
	
	abstract E newSingle();

}
