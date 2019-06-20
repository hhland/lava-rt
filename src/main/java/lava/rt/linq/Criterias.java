package lava.rt.linq;

import static org.junit.Assume.assumeNotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lava.rt.common.ReflectCommon;
import lava.rt.common.TextCommon;


public abstract class Criterias {

	
	abstract String concat(Collection<String> columns);
	
	abstract String toPaging(String sql,int start,int size);
	
    protected static final Map<Class,String> clsJoinAsPropMap=new HashMap<>();
	
	public final static Criterias mssql=new Criterias() {
		
		@Override
		String toPaging(String sql,int start,int size) {
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
		String concat(Collection<String> columns) {
			// TODO Auto-generated method stub
			String ret="CONCAT("+String.join(",", columns)+")";
			return ret;
		}
	};
	
   public final static Criterias mysql=new Criterias() {
		
		@Override
		String toPaging(String sql,int start,int size) {
			// TODO Auto-generated method stub
			StringBuffer ret=new StringBuffer(sql);
			ret.append(start).append(",").append(size);
			return ret.toString();
		}
		
		@Override
		String concat(Collection<String> columns) {
			// TODO Auto-generated method stub
			String ret=String.join("+", columns);
			return ret;
		}
	};
	
    public final static Criterias oracle=new Criterias() {
		
		@Override
		String toPaging(String sql,int start,int size) {
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
		String concat(Collection<String> columns) {
			// TODO Auto-generated method stub
			String ret=String.join("||", columns);
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
			ret[i]= column+" as "+column.propName;
			i++;
		}
		return String.join(",", ret);
	}
	
	public static String tableName(Class cls){
		String para=cls.getSimpleName();
        StringBuilder sb=new StringBuilder(para);
        int temp=0;//定位
        
        for(int i=0;i<para.length();i++){
              if(Character.isUpperCase(para.charAt(i))&&i>0){
                    sb.insert(i+temp, "_");
                    temp+=1;
              }
        }
        
        return sb.toString().toUpperCase();
    }

	
	public static String joinColumnAsProp(Class<Entity> entityCls) {
		String sql=clsJoinAsPropMap.get(entityCls);
		if(sql==null) {
			Map<String,Field> fieldMap= ReflectCommon.theDeclaredFieldMap(entityCls);
			List<String> joins=new ArrayList<>();
			for(Entry<String, Field> ent:fieldMap.entrySet()) {
				String cn=ent.getValue().getName(),asn=Column.toClassName(cn);
				joins.add(cn+" as "+asn);
				
			}
			sql=String.join(",", joins);
			clsJoinAsPropMap.put(entityCls, sql);
		}
		return sql;
	}
	
}
