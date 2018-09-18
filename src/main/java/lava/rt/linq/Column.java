package lava.rt.linq;

import java.util.Date;

import lava.rt.common.TextCommon;

public class Column {

	private String column;
	
	
	public Column(String column) {
		this.column=column;
		
	}
	
	public String eq(Object val) {return sql_eq(true,column,val);}
	public String notEq(Object val) {return sql_eq(false,column,val);}
	public String lt(Object val) {return sql_lt(true,column,val);}
	public String notLt(Object val) {return sql_lt(false,column,val);}
	public String gt(Object val) {return sql_gt(true,column,val);}
	public String notGt(Object val) {return sql_gt(false,column,val);}
	public String isNull() {return sql_isnull(true,column);}
	public String isNotNull() {return sql_isnull(false,column);}
	public <T> String in(T... vals) {return sql_in(true,column,vals);}
	public <T> String notIn(T...vals) {return sql_in(false,column,vals);}
	public <T> String between(T from,T to) {return sql_between(true,column,from,to);}
	public <T> String notBetween(T from,T to) {return sql_between(false,column,from,to);}
	public String like(String val) {return sql_like(true, column,val);}
	public String notLike(String val) {return sql_like(false, column,val);}	
	
	public String asc() {
		return " "+column + " asc";
	}
	
	public String desc() {
		return " "+column + " desc";
	}
	
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.column;
	}
	
	
	
	protected  String sql_eq(boolean bl,String column,Object val) {
		String str=sql_val(val);
		return " "+column+ (bl?"":"!")+ " = "+ str;
	}
	
	protected static String sql_like(boolean bl,String column,String val) {
		String str="";
		if(val==null) str="?";
		else str="'"+val+"'";
		return " "+column+ (bl?" ":" not")+ " like "+ str;
	}
	
	
	protected  String sql_lt(boolean bl,String column,Object val) {
		String str=sql_val(val);
		return " "+column+ (bl?" ":" !")+ " < "+ str;
	}
	
	protected  String sql_gt(boolean bl,String column,Object val) {
		String str=sql_val(val);
		return " "+column+ (bl?" ":" !")+ " > "+ str;
	}
	
	protected  <T> String sql_between(boolean bl,String column,T from,T to) {
		String fromStr=sql_val(from),toStr=sql_val(to);
		return " "+column+ (bl?" ":" not")+ " between "+ fromStr+ " and " +toStr;
	}
	
	protected  <T> String sql_in(boolean bl,String column,T...vals) {
		String[] strs=new String[vals.length] ;
		for(int i=0;i<strs.length;i++) {
			strs[i]=sql_val(vals[i]);
		}
		return " "+column+ (bl?" in ":" not in ")+ " ( "+String.join(",", strs)+" )";
	}
	
	
	
	protected static String sql_isnull(boolean bl,String column) {
		return " "+column+ (bl?" is ":" is not ")+ " null ";
	}
	
	
	
	private  String sql_val(Object val) {
		String str="";
		if(val==null) str="?";
		else if(val instanceof String) str="'"+val.toString()+"'";
		
		else str=val.toString();
		return str;
	}
	
	
	
}
