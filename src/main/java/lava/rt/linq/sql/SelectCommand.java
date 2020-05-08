package lava.rt.linq.sql;

import java.io.Serializable;

public class SelectCommand implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private  String columns, from,where, orderby;
	
	public  Object[] param;
	

	public SelectCommand(String columns, String from, Object... param) {
		super();
		this.columns = columns;
		this.from = from;
		this.param=param;
	}
	
	public SelectCommand(String columns, String from,String where, String orderby, Object... param) {
		super();
		this.columns = columns;
		this.from = from;
		this.where=where;
		this.orderby = orderby;
		this.param=param;
	}
	
	

	@Override
	public String toString() {
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

	public String getColumns() {
		return columns;
	}

	public String getFrom() {
		return from;
	}

	public String getWhere() {
		return where;
	}

	public String getOrderby() {
		return orderby;
	}
	
	
	
	
}
