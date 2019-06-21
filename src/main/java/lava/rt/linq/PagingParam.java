package lava.rt.linq;

import java.io.Serializable;

public class PagingParam implements Serializable{

	
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	public  int start,limit;
	
	public  String psql,sql;
	
	public  Object[] param;

	public PagingParam(Criterias criterias, int start, int limit, String sql, Object... param) {
		super();
		this.start = start;
		this.limit = limit;
		this.psql =criterias.toPaging(sql, start, limit);
		this.sql = sql;
		this.param = param;
	}

	
	public PagingParam(String psql, int start, int limit, String sql, Object... param) {
		super();
		this.start = start;
		this.limit = limit;
		this.psql =psql;
		this.sql = sql;
		this.param = param;
	}
	
	
	
	
	
	
}
