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
import lava.rt.linq.Entity;
import lava.rt.linq.execption.CommandExecuteExecption;
import lava.rt.linq.sql.Criteria.Column;


public   class  View<M extends Entity> {

	protected final DataSourceContext dataContext;
	
	public final String tableName;
	protected final Class<M> entryClass;
	protected final Map<String,Field> entityFieldMap=new HashMap<>();
	
	protected static Map<Class,List<Column>> clsColumns=new HashMap<>();
	
	//protected final Map<String,Long> entryFieldOffsetMap=new HashMap<>();
	
	protected final String sqlSelect;
	
	
	
	//protected static UnsafeAdapter unsafeAdapter= UnsafeAdapter.getInstance();
	
	protected View (DataSourceContext dataContext,Class<M> entryClass,String tableName) {
		this.dataContext=dataContext;
		this.tableName=tableName;
		this.entryClass=entryClass;
		//this.entityFieldMap=ReflectCommon.theDeclaredFieldMap(entryClass);
		
		for(Entry<String, Field> ent :ReflectCommon.getTheDeclaredFieldMap(entryClass).entrySet()){
			Field field=ent.getValue();
			boolean isStatic = ReflectCommon.isStatic(field);
			if(isStatic)continue;
			this.entityFieldMap.put(ent.getKey(), ent.getValue());
			//entryFieldOffsetMap.put(ent.getKey(), unsafeAdapter.objectFieldOffset(ent.getValue()));
		}
		
		
		
		entityFieldMap.forEach((k,v)->v.setAccessible(true));
		
		sqlSelect="select * from "+tableName;
		
	}
	
	
	
   public void foreach(ResultHandler<M> handler,String where,String orderBy,Object...params) throws CommandExecuteExecption{
    	
    	StringBuffer sql=new StringBuffer(sqlSelect);
    	if(where!=null) {
    	   sql.append(" where ").append(where);
    	}
    	if(orderBy!=null) {
     	   sql.append(" order by ").append(orderBy);
     	}
		dataContext.foreachEntities(entryClass,handler,sql.toString(),params);
	}
    
    
     public List<M> select(String where,String orderBy,Object...params) throws CommandExecuteExecption{
    	
    	StringBuffer sql=new StringBuffer(sqlSelect);
    	if(where!=null) {
    	   sql.append(" where ").append(where);
    	}
    	if(orderBy!=null) {
     	   sql.append(" order by ").append(orderBy);
     	}
		return dataContext.listEntities(entryClass,sql.toString(),params);
	}
    
    
    public List<M> selectByPaging(Criterias criterias,int start,int limit,String where,String orderBy,Object...params) throws CommandExecuteExecption{
    	
    	StringBuffer sql=new StringBuffer(sqlSelect);
    	if(where!=null) {
    	   sql.append(" where ").append(where);
    	}
    	if(orderBy!=null) {
     	   sql.append(" order by ").append(orderBy);
     	}
    	
    	String psql=criterias.toPaging(sql.toString(),start,limit);
		return dataContext.listEntities(entryClass,psql,params);
	}
	
	





	@Override
	public String toString() {
		return tableName;
	}




	public View<M> duplicate(String tableName){
		View<M> ret=new View<>(this.dataContext, this.entryClass, tableName);
		return ret;
	}

	
	
	
	
	
	public static List<Column> getColumns(Class<? extends Entity> entityCls) {
	    List<Column> ret=clsColumns.get(entityCls);
	    if(ret==null) {
	    ret=new ArrayList<>();
		Map<String,Field> fieldMap= ReflectCommon.getTheDeclaredFieldMap(entityCls);
		
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






	public M newEntity() throws Exception {
		// TODO Auto-generated method stub
	    
		return ReflectCommon.newEntity(entryClass);
	}
}
