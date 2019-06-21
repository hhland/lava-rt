package lava.rt.linq;

import java.io.Serializable;

public class PagingParam<C extends Criterias> implements Serializable{

	
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final Criterias criterias;
	
	public final int start,limit;

	public PagingParam(Criterias criterias, int start, int limit) {
		super();
		this.criterias = criterias;
		this.start = start;
		this.limit = limit;
	}
	
	public String toPaging(String sql) {
		String ret=criterias.toPaging(sql, start, limit);
		return ret;
	}
	
}
