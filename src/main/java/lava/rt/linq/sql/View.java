package lava.rt.linq.sql;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lava.rt.common.ReflectCommon;
import lava.rt.linq.CommandExecuteExecption;
import lava.rt.linq.Entity;


public   class  View<M extends Entity> {

	protected final DataSourceContext dataContext;
	
	public final String tableName;
	protected final Class<M> entryClass;
	protected final Map<String,Field> entryFieldMap=new HashMap<>();
	
	protected static Map<Class,List<Column>> clsColumns=new HashMap<>();
	
	//protected final Map<String,Long> entryFieldOffsetMap=new HashMap<>();
	
	protected final String sqlSelect;
	
	
	
	//protected static UnsafeAdapter unsafeAdapter= UnsafeAdapter.getInstance();
	
	protected View (DataSourceContext dataContext,Class<M> entryClass,String tableName) {
		this.dataContext=dataContext;
		this.tableName=tableName;
		this.entryClass=entryClass;
		//this.entryFieldMap=ReflectCommon.theDeclaredFieldMap(entryClass);
		
		for(Entry<String, Field> ent :ReflectCommon.theDeclaredFieldMap(entryClass).entrySet()){
			Field field=ent.getValue();
			boolean isStatic = ReflectCommon.isStatic(field);
			if(isStatic)continue;
			this.entryFieldMap.put(ent.getKey(), ent.getValue());
			//entryFieldOffsetMap.put(ent.getKey(), unsafeAdapter.objectFieldOffset(ent.getValue()));
		}
		
		
		
		entryFieldMap.forEach((k,v)->v.setAccessible(true));
		
		sqlSelect="select * from "+tableName;
	}
	
	
	
	
    
    
    public List<M> select(String where,Object...params) throws CommandExecuteExecption{
    	
    	String sql=sqlSelect+" "+where;
		return dataContext.entityList(entryClass,sql,params);
	}
    
    public List<M> selectByPaging(Criterias criterias,int start,int limit,String where,Object...params) throws CommandExecuteExecption{
    	
    	String sql=sqlSelect+" "+where;
    	sql=criterias.toPaging(sql,start,limit);
		return dataContext.entityList(entryClass,sql,params);
	}
	
	





	@Override
	public String toString() {
		return "View [tableName=" + tableName + ", entryClass=" + entryClass + "]";
	}




	public View<M> duplicate(String tableName){
		View<M> ret=new View<>(this.dataContext, this.entryClass, tableName);
		return ret;
	}

	
	final static String elPrefix="{view:",elSubFix="}";
	final static Pattern elPattern = Pattern.compile("\\"+elPrefix+"(.*?)\\"+elSubFix);
	
	public static String toEl(Class<? extends Entity> cls) {
		String ret=elPrefix+cls.getName()+elSubFix;
		return ret;
	}
	
	protected static String formatEl(String sql,Map<Class,View> viewMap) {
		String ret=sql;
		
	      
	     Matcher matcher= elPattern.matcher(ret);
		 
	      
	      if(!matcher.find())return ret;
	      for(int i=0;i<matcher.groupCount();i++) {
	    	  
	    	  String groupi= matcher.group(i);
	    	  String cn=groupi.substring(elPrefix.length(),groupi.length()-elSubFix.length());
	    	  
	    	  for(Entry<Class, View> ent :viewMap.entrySet()) {
	    		  if(ent.getKey().getName().equals(cn)) {
	    			  ret=ret.replace(groupi, ent.getValue().tableName);
	    			  break;
	    		  }
	    	  }
	    	  
	      }
		return ret;
	}
	
	
	public static List<Column> getColumns(Class<? extends Entity> entityCls) {
	    List<Column> ret=clsColumns.get(entityCls);
	    if(ret==null) {
	    ret=new ArrayList<>();
		Map<String,Field> fieldMap= ReflectCommon.theDeclaredFieldMap(entityCls);
		
		for(Entry<String, Field> ent:fieldMap.entrySet()) {
			boolean isStatic = ReflectCommon.isStatic(ent.getValue());
			if(isStatic)continue;
			String key=ent.getKey();
			Column column=new Column(key);
			ret.add( column);
		 }
	    }
	
	return ret;
}
}
