package lava.rt.linq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lava.rt.common.TextCommon;

public final class Criterias {

	
    private static final Map<Class,Set<Column>> clsColumnsMap=new HashMap<>();
	
	public static String groupBy(Column... columns) {
		return " group by "+TextCommon.join(",", columns);
	}
	
	public static String orderBy(Object... columns) {
		return " order by "+TextCommon.join(",", columns);
	}
	
	public static String distinct(Column...columns) {
		return " distinct "+TextCommon.join(",", columns);
	}
	
	
	public static String asProp(Column...columns) {
		String[] ret=new String[columns.length];
		for(int i=0;i<ret.length;i++) {
			Column column=columns[i];
			ret[i]= column+" as "+column.propName;
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

	
}
