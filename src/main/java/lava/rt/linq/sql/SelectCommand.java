package lava.rt.linq.sql;

import java.io.Serializable;

public class SelectCommand implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String columns, from,where, orderby;
	
	
	
	private  int start=0,limit=0;
	
	private  String sql,countSql;
	
    private Criterias criterias;
	
	
	public SelectCommand(Criterias criterias,String columns, String from,String where, String orderby) {
		super();
		this.columns = columns;
		this.from = from;
		this.where=where;
		this.orderby = orderby;
		
		this.sql=toSql();
		this.countSql=toCountSql();
		this.criterias=criterias;
	}
	
	

	
	protected String toSql() {
		StringBuffer ret=new StringBuffer();
		ret.append("select ")
		.append(columns)
		.append(" from ")
		.append(from);
		if(where!=null) {
		  ret.append(" where ").append(where);
		}
		if(orderby!=null) {
		 ret.append(" order by ").append(orderby);
					
		}
		return ret.toString();
	}
	
	protected String getSql() {
		String ret=sql;
		if(start<0||limit<=0||criterias==null) {
			return ret;
		}
		ret=criterias.toPaging(sql, start, limit);
		return ret.toString();
	}
	
	protected String toCountSql() {
		StringBuffer ret=new StringBuffer();
		ret.append("select ")
		.append(" count(*) ")
		.append(" from ")
		.append(from);
		if(where!=null) {
		  ret.append(" where ").append(where);
		}
		
		return ret.toString();
	}

	




	public int getStart() {
		return start;
	}




	public void setStart(int start) {
		this.start = start;
	}




	public int getLimit() {
		return limit;
	}




	public void setLimit(int limit) {
		this.limit = limit;
	}




	public String getCountSql() {
		return countSql;
	}
	
	
	
	
}
