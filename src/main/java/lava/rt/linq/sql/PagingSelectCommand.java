package lava.rt.linq.sql;

import java.io.Serializable;
import java.util.List;

import lava.rt.sqlparser.SelectSqlParser;
import lava.rt.sqlparser.SqlSegment;

public class PagingSelectCommand  implements Serializable{

	
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	public  int start,limit;
	
	private  SelectCommand command;
	
	private  String pagingSql;
	
	public  Object[] param;

	public PagingSelectCommand(Criterias criterias, int start, int limit, SelectCommand command, Object... param) {
		this.start = start;
		this.limit = limit;
		this.pagingSql =criterias.toPaging(command.toString(), start, limit);
		this.command=command;
		this.param = param;
	}
	
	

	
	protected SelectCommand createSelectCountCommand() {
		SelectCommand ret=new SelectCommand("count(*)", command.getFrom(),command.getWhere(),command.getWhere());
        return ret;
	}
	
	
	@Deprecated
    protected  String createSelectCountSql() {
		
    	String sql=command.toString();
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




	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return pagingSql;
	}
	
	
	
}
