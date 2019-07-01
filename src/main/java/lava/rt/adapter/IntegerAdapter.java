package lava.rt.adapter;

public class IntegerAdapter extends BaseAdapter<Integer>{

	public IntegerAdapter(Integer _this) {
		super(_this);
		// TODO Auto-generated constructor stub
	}
	
	
	private static int[] int64=new int[64];
	
	static {
		
		for(int i=0;i<int64.length;i++) {
			int64[i]=2^i;
		}
		
	}
	
	
	public boolean contains(Integer val) {
		boolean ret=false;
		return ret;
	}

	
	public void add(int val) {
		
	}
	
    public void remove(int val) {
		
	}
	
}
