package lava.rt.linq.sql;

public class SelectParam {

	
	private  String columns, from,where, orderby;

	public SelectParam(String columns, String from) {
		super();
		this.columns = columns;
		this.from = from;
	}
	
	public SelectParam(String columns, String from,String where, String orderby) {
		super();
		this.columns = columns;
		this.from = from;
		this.where=where;
		this.orderby = orderby;
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
	
	
	
	
}
