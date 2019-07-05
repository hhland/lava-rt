package lava.rt.linq;

import java.io.Serializable;
import java.util.List;

import lava.rt.sqlparser.SelectSqlParser;
import lava.rt.sqlparser.SqlSegment;

public class PagingParam implements Serializable{

	
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	public  int start,limit;
	
	public  String sql,pagingSql;
	
	public  Object[] param;

	public PagingParam(Criterias criterias, int start, int limit, String sql, Object... param) {
		super();
		this.start = start;
		this.limit = limit;
		this.pagingSql =criterias.toPaging(sql, start, limit);
		this.sql = sql;
		this.param = param;
		
	}

	
	public PagingParam(String psql, int start, int limit, String sql, Object... param) {
		super();
		this.start = start;
		this.limit = limit;
		this.pagingSql =psql;
		this.sql = sql;
		this.param = param;
	
	}
	
	
    protected  String countSql() {
		
		StringBuffer ret=new StringBuffer();
			String csql="select count(*) "+sql.substring(sql.toLowerCase().indexOf("from"));
			SelectSqlParser parser=new SelectSqlParser(csql);
		    List<SqlSegment> segments=	parser.getSegments();
		    for(SqlSegment segment:segments) {
		    	if(segment.getStart().toLowerCase().equals("order by")) continue;
		    	ret.append(" ").append(segment.toSql());
		    }
		    
		
		
		return ret.toString();
	}
	
	
	
}
