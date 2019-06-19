package lava.rt.linq;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lava.rt.common.TextCommon;
import net.sourceforge.jtds.jdbc.ColInfo;

public final class Criterias {

	
    protected static final Map<Class,Map<String,Column>> clsColumnsMap=new HashMap<>();
	
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

	
	protected static void addColumn(Class cls,Column column) {
		Map<String,Column> columnSet=clsColumnsMap.get(cls);
		if(columnSet==null) {
			columnSet=new HashMap<>();
		}
		columnSet.put(column.column,column);
	}
	
	protected static Map<String,Column> getColumnMap(Class cls){
		return clsColumnsMap.get(cls);
	}
	
}
