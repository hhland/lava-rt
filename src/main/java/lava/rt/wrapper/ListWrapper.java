package lava.rt.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ListWrapper<E> extends BaseWrapper<List<E>>{

	public ListWrapper(List<E> _this) {
		super(_this);
		// TODO Auto-generated constructor stub
		this.total=self.size();
	}

	
	
	private int total=0;
	
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


	public int getTotal() {
		return total;
	}


	public void setTotal(int total) {
		this.total = total;
	}
	
	
	
	
}
