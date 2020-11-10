package lava.rt.linq.sql;

import java.io.Serializable;
import java.util.Objects;

import lava.rt.common.LangCommon;

public class SelectCommand implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String columns, from,where, orderby;
	
	
	
	
	
	protected  String sql,countSql;
	
    
	
    
    public SelectCommand(String columns, String from,String where, String orderby) {
		super();
		this.columns = columns;
		this.from = from;
		this.where=where;
		this.orderby = orderby;
		
		this.sql=toSql();
		this.countSql=toCountSql();
		
	}
	
	
	
	

	
	protected String toSql() {
		StringBuffer ret=new StringBuffer();
		ret.append("select ")
		.append(columns)
		.append(" from ")
		.append(from);
		if(!isBlank(where)) {
		  ret.append(" where ").append(where);
		}
		if(!isBlank(orderby)) {
		 ret.append(" order by ").append(orderby);
					
		}
		return ret.toString();
	}
	
	private boolean isBlank(String value) {
		// TODO Auto-generated method stub
		return LangCommon.isAnyBlank(value);
	}






	protected String getSql() {
		return this.sql;
	}
	
	protected String toCountSql() {
		StringBuffer ret=new StringBuffer();
		ret.append("select ")
		.append(" count(*) ")
		.append(" from ")
		.append(from);
		if(!isBlank(where)) {
		  ret.append(" where ").append(where);
		}
		
		return ret.toString();
	}

	


	public String getCountSql() {
		return countSql;
	}
	
	
	public PagingSelectCommand createPagingSelectCommand(Criterias criterias,int start,int limit) {
		PagingSelectCommand ret=new PagingSelectCommand(criterias,start,limit, columns, from, where, orderby);
		return ret;
	}
	
	
	
	
	public class PagingSelectCommand extends SelectCommand{

		
		private Criterias criterias;
		
		private  int start=0,limit=0;
		
		protected PagingSelectCommand(Criterias criterias,int start,int limit, String columns, String from, String where, String orderby) {
			super( columns, from, where, orderby);
			// TODO Auto-generated constructor stub
			this.criterias=criterias;
			this.start=start;
			this.limit=limit;
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




		
		protected String toPaginSql() {
			// TODO Auto-generated method stub
			String ret=sql;
			
			ret=criterias.toPaging(sql, start, limit);
			return ret.toString();
			
		}




		@Override
		protected String getSql() {
			// TODO Auto-generated method stub
			return super.getSql();
		}
		
	}
	
	
	
}
