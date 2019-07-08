package lava.rt.linq.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Criteria {

	
	
	public String eq(Object val) ;
	public String notEq(Object val) ;
	public String lt(Object val) ;
	public String notLt(Object val) ;
	public String gt(Object val) ;
	public String notGt(Object val) ;
	public String isNull() ;
	public String isNotNull() ;
	public <T> String in(T... vals) ;
	public <T> String notIn(T...vals) ;
	public <T> String between(T from,T to) ;
	public <T> String notBetween(T from,T to) ;
	public String like(String val);
	public String notLike(String val);
	
	
	public String as(String name) ;

	
	
	
	
	 static String sql_eq(boolean bl,String column,Object val) {
		String str=sql_val(val);
		StringBuffer ret=new StringBuffer("");
		ret
		.append(column)
		.append(bl?"":"!")
		.append(" = ")
		.append(str)
		;
		return ret.toString();
	}
	
	static String sql_like(boolean bl,String column,String val) {
		String str=sql_val(val);
		StringBuffer ret=new StringBuffer("");
		ret
		.append(column)
		.append(bl?"":"not")
		.append(" like ")
		.append(str)
		;
		return ret.toString();
		
	}
	
	
	static String sql_lt(boolean bl,String column,Object val) {
		String str=sql_val(val);
		StringBuffer ret=new StringBuffer("");
		ret
		.append(column)
		.append(bl?"":"!")
		.append(" < ")
		.append(str)
		;
		return ret.toString();
	}
	
	static String sql_gt(boolean bl,String column,Object val) {
		String str=sql_val(val);
		StringBuffer ret=new StringBuffer("");
		ret
		.append(column)
		.append(bl?"":"!")
		.append(" > ")
		.append(str)
		;
		return ret.toString();
	}
	
	static <T> String sql_between(boolean bl,String column,T from,T to) {
		String fromStr=sql_val(from),toStr=sql_val(to);
		StringBuffer ret=new StringBuffer("");
		ret
		.append(column)
		.append(bl?" ":" not")
		.append(" between ")
		.append(fromStr)
		.append(" and ")
		.append(toStr)
		;
		return ret.toString();
		
	}
	
	static <T> String sql_in(boolean bl,String column,T...vals) {
		String[] strs=new String[vals.length] ;
		for(int i=0;i<strs.length;i++) {
			strs[i]=sql_val(vals[i]);
		}
		StringBuffer ret=new StringBuffer("");
		ret
		.append(column)
		.append(bl?" in ":" not in ")
		.append("(")
		.append(String.join(",", strs))
		.append(")")
		;
		return ret.toString();
	}
	
	
	
	static String sql_isnull(boolean bl,String column) {
		StringBuffer ret=new StringBuffer("");
		ret
		.append(column)
		.append(bl?" is ":" is not ")
		.append(" null ")
		;
		return ret.toString();
		
	}
	
	
	
	static String sql_val(Object val) {
		String str="";
		if(val==null) str="?";
		else if(val instanceof String) str="'"+val.toString()+"'";
		
		else str=val.toString();
		return str;
	}
	
	public static String toPropName(String name) {
		String ret=toClassName(name);
		
		return ret.substring(0, 1).toLowerCase()+ret.substring(1, ret.length());
	}
	
	static String toClassName(String name) {
		StringBuffer ret=new StringBuffer("");
		for(String _colName:name.split("_")) {
			ret
			.append(_colName.substring(0, 1).toUpperCase())
			.append(_colName.substring(1).toLowerCase());
			
		}
		if(name.endsWith("_")) {
			ret.append("_");
		}
		return ret.toString();
	}
	
	final static String elPrefix="{criteria:",elSubFix="}";
	final static Pattern elPattern = Pattern.compile("\\"+elPrefix+"(.*?)\\"+elSubFix);
	
	
	public static String formatEl(String command, Map<String, String> columnMap) {
		// TODO Auto-generated method stub
		String ret=command;
		
	      
	     Matcher matcher= elPattern.matcher(ret);
		 
	      
	      if(!matcher.find())return ret;
	      for(int i=0;i<matcher.groupCount();i++) {
	    	  
	    	  String groupi= matcher.group(i);
	    	  String cn=groupi.substring(elPrefix.length(),groupi.length()-elSubFix.length());
	    	  
	    	  for(Entry<String, String> ent :columnMap.entrySet()) {
	    		  if(ent.getKey().equals(cn)) {
	    			  ret=ret.replace(groupi, ent.getValue());
	    			  break;
	    		  }
	    	  }
	    	  
	      }
		return ret;
	}
	
	
	
	
}