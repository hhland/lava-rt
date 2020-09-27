package lava.rt.wrapper;

import java.util.ArrayList;
import java.util.Random;

public class ArrayListWrapper<E> extends BaseWrapper<ArrayList<E>>{

	public ArrayListWrapper(ArrayList<E> _this) {
		super(_this);
		// TODO Auto-generated constructor stub
	}

	
	
	
	
    private Integer index=0;
	
	
    private final Random random=new Random();
	

	
	
	
	public E getNext() {
		synchronized(index) {
			return self.get(index++%self.size());
		}
	}
	
	
	public E getRandom() {
		synchronized(random) {
			return self.get(random.nextInt(self.size()));
		}
	}
	
	
}
