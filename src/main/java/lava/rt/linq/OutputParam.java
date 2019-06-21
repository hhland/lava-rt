package lava.rt.linq;

import java.io.Serializable;
import java.sql.Types;

public class  OutputParam<E> implements Serializable{
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public E result,value;
    public final int sqlType;
    final Class<E> reslutCls;
	
    public OutputParam(E value) {
		super();
		this.value=value;
		this.reslutCls=(Class<E>)value.getClass();
		if(reslutCls== Integer.class) {
			sqlType=Types.INTEGER;
		}else {
			sqlType=Types.VARCHAR;
		}
	}
    
	public OutputParam(Class<E> reslutCls) {
		super();
		this.reslutCls=reslutCls;
		if(reslutCls== Integer.class) {
			sqlType=Types.INTEGER;
		}else {
			sqlType=Types.VARCHAR;
		}
	}
	 
	
	 
}