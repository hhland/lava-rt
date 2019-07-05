package lava.rt.linq;

import static org.junit.Assume.assumeNotNull;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lava.rt.common.ReflectCommon;
import lava.rt.common.TextCommon;
import lava.rt.sqlparser.SelectSqlParser;
import lava.rt.sqlparser.SqlSegment;


public abstract class Criterias implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static SimpleDateFormat SDF_YMD=new SimpleDateFormat("yyyy-MM-dd")
			,SDF_YMDHMS=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			;
	
	public abstract String concat(Collection<String> columns);
	
	public abstract String toPaging(String sql,int start,int limit);
	
	public abstract String format(Date date);
	
   
	public final static Criterias mssql=new Criterias() {
		
		@Override
		public String toPaging(String sql,int start,int size) {
			// TODO Auto-generated method stub
			//SELECT id,dtime FROM dbo.TestTab ORDER BY id OFFSET 1 ROWS FETCH NEXT 100 ROWS ONLY
			StringBuffer ret=new StringBuffer(sql);
			ret
			.append(" OFFSET ")
			.append(start)
			.append(" ROWS FETCH NEXT ")
			.append(size)
			.append(" ROWS ONLY")
			;
			return ret.toString();
		}
		
		@Override
		public String concat(Collection<String> columns) {
			// TODO Auto-generated method stub
			String ret="CONCAT("+String.join(",", columns)+")";
			return ret;
		}

		@Override
		public String format(Date date) {
			// TODO Auto-generated method stub
			String ret="convert(datetime,'"+SDF_YMDHMS.format(date)+"', 20)";
			return ret;
		}
	};
	
   public final static Criterias mysql=new Criterias() {
		
		@Override
		public String toPaging(String sql,int start,int size) {
			// TODO Auto-generated method stub
			StringBuffer ret=new StringBuffer(sql);
			ret.append(start).append(",").append(size);
			return ret.toString();
		}
		
		@Override
		public String concat(Collection<String> columns) {
			// TODO Auto-generated method stub
			String ret=String.join("+", columns);
			return ret;
		}

		@Override
		public String format(Date date) {
			// TODO Auto-generated method stub
			String ret="'"+SDF_YMD.format(date)+"'";
			return ret;
		}
	};
	
    public final static Criterias oracle=new Criterias() {
		
		@Override
		public String toPaging(String sql,int start,int size) {
			// TODO Auto-generated method stub
			/*SELECT *

			  FROM (SELECT ROWNUM AS rowno, t.*

			          FROM emp t

			         WHERE hire_date BETWEEN TO_DATE ('20060501', 'yyyymmdd')

			                             AND TO_DATE ('20060731', 'yyyymmdd')

			           AND ROWNUM <= 20) table_alias

			 WHERE table_alias.rowno >= 10;
			*/
			String rsql=sql.trim().substring(6);
			StringBuffer ret=new StringBuffer("select * from ( select ROWNUM AS rowno,");
			ret.append(rsql)
			.append(" AND ROWNUM <= ")
			.append(size+start)
			.append(" ) WHERE rowno >=")
			.append(start);
			return ret.toString();
		}
		
		@Override
		public String concat(Collection<String> columns) {
			// TODO Auto-generated method stub
			String ret=String.join("||", columns);
			return ret;
		}

		@Override
		public String format(Date date) {
			// TODO Auto-generated method stub
			String ret="to_date('"+SDF_YMD.format(date)+"','yyyy-mm-dd')";
			return ret;
		}
	};
	
    
	
	public static String groupBy(Collection<Column> columns) {
		return " group by "+join(columns);
	}
	
	public static String orderBy(Collection<Column> columns) {
		return " order by "+join(columns);
	}
	
	public static String distinct(Collection<Column> columns) {
		return " distinct "+join(columns);
	}
	
	
	public static String join(Collection<Column> columns) {
		String[] ret=new String[columns.size()];
		int i=0;
		for(Column column:columns) {
		//for(int i=0;i<ret.length;i++) {
		//	Column column=columns[i];
			ret[i]= column.column;
			i++;
		}
		return String.join(",", ret);
	}
	
	public static String joinAsProp(Collection<Column> columns) {
		String[] ret=new String[columns.size()];
		int i=0;
		for(Column column:columns) {
		//for(int i=0;i<ret.length;i++) {
		//	Column column=columns[i];
			ret[i]= column.as(column.propName);
			i++;
		}
		return String.join(",", ret);
	}
	
	

	
	
	
	
	
}
