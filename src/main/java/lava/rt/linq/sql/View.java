package lava.rt.linq.sql;

import java.lang.reflect.Field;


import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import lava.rt.common.ReflectCommon;
import lava.rt.linq.CommandExecuteExecption;
import lava.rt.linq.Entity;



public abstract  class  View<M extends Entity> {

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
	
	
	
   public void foreach(BiFunction<Integer,M,Integer> handler,String where,String orderBy,Object...params) throws CommandExecuteExecption{
    	SelectCommand cmd=new SelectCommand(null, "*", tableName, where, orderBy);
    	
		dataContext.foreachEntities(entryClass,handler,cmd,params);
	}
    
    
     public List<M> select(String where,String orderBy,Object...params) throws CommandExecuteExecption{
    	 SelectCommand cmd=new SelectCommand(null, "*", tableName, where, orderBy);
    	 List<M> ret=dataContext.listEntities(entryClass,cmd,params);
    	 return ret;
	}
    
    
    public List<M> select(Criterias criterias,int start,int limit,String where,String orderBy,Object...params) throws CommandExecuteExecption{
    	
    	SelectCommand cmd=new SelectCommand(criterias, "*", tableName, where, orderBy);
    	cmd.setStart(start);
    	cmd.setLimit(limit);
		return dataContext.listEntities(entryClass,cmd,params);
	}
	
	
    public int createTable(String newTableName,String where,Object... param) throws CommandExecuteExecption{
		StringBuffer sql=new StringBuffer(" CREATE TABLE ");
		sql
		.append(newTableName)
		.append(" SELECT * FROM ")
		.append(this.tableName)
		;
		if(where!=null) {
			sql.append(" where ").append(where);
		}
		int ret=dataContext.executeUpdate(sql.toString(), param);
		return ret;
	}


    public int insertInto(String newTableName,String where,Object...param) throws CommandExecuteExecption{
		//Table<M> ret=null;
		StringBuffer sql=new StringBuffer(" insert into ");
		sql.append(newTableName)
		
		.append(" select * from ")
		.append(tableName);
		if(where!=null) {
			sql.append(" where ").append(where);
		}
		int ret= dataContext.executeUpdate(sql.toString(), param);
		//ret=new Table<>(this.dataContext, this.entryClass, newTableName, pkName);
		return ret;
	}

	



    


	public abstract M newEntity() throws Exception;
}
