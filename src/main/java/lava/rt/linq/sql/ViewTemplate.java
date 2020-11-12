package lava.rt.linq.sql;

import java.io.Serializable;
import java.lang.reflect.Field;


import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import lava.rt.common.ReflectCommon;
import lava.rt.linq.CommandExecuteExecption;
import lava.rt.linq.Entity;
import lava.rt.linq.sql.SelectCommand.PagingSelectCommand;
import lava.rt.wrapper.ListWrapper;



public abstract  class  ViewTemplate<M extends Entity> {

	protected final DataSourceContext dataContext;
	
	public final String tableName;
	protected final Class<M> entryClass;
	protected final Map<String,Field> entityFieldMap=new HashMap<>();
	
	
	
	//protected final Map<String,Long> entryFieldOffsetMap=new HashMap<>();
	
	protected final String sqlSelect;
	
	
	
	//protected static UnsafeAdapter unsafeAdapter= UnsafeAdapter.getInstance();
	
	protected ViewTemplate (DataSourceContext dataContext,Class<M> entryClass,String tableName) {
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
	
	
	
   public void selectCursoring(BiFunction<Integer,M,Integer> cursor,String where,String orderBy,Serializable...params) throws CommandExecuteExecption{
    	SelectCommand cmd=new SelectCommand("*", tableName, where, orderBy);
        cmd.setParams(params);
        selectCursoring(cursor,cmd);
	}
   
   
   public void selectCursoring(BiFunction<Integer,M,Integer> cursor,SelectCommand cmd) throws CommandExecuteExecption{
   	
		dataContext.cursoringEntities(cursor,entryClass,cmd);
	}
    
    
    public List<M> selectList(String where,String orderBy,Serializable...params) throws CommandExecuteExecption{
    	 SelectCommand cmd=new SelectCommand( "*", tableName, where, orderBy);
    	 cmd.setParams(params);
    	 List<M> ret=selectList(cmd);
    	
    	 return ret;
	}
    
    public List<M> selectList(SelectCommand cmd) throws CommandExecuteExecption{
   	 
   	 List<M> ret=dataContext.listEntities(entryClass,cmd);
   	
   	 return ret;
	}
     
     
     public List<M> selectList(String where,String orderBy,int start,int limit,Serializable...params) throws CommandExecuteExecption{
    	 SelectCommand cmd=new SelectCommand( "*", tableName, where, orderBy);
    	 PagingSelectCommand pcmd=cmd.createPagingSelectCommand(dataContext.getCriterias(),start,limit);
    	 pcmd.setParams(params);
    	 List<M> ret=selectList(pcmd);
    	 return ret;
	}
     
     public List<M> selectList(PagingSelectCommand pcmd) throws CommandExecuteExecption{
    	 ListWrapper<M> ret=dataContext.listEntities(entryClass,pcmd);
    	 return ret.self;
	}
    
    
    public ListWrapper<M> selectPaging(String where,String orderBy,int start,int limit,Serializable...params) throws CommandExecuteExecption{
    	SelectCommand cmd=new SelectCommand( "*", tableName, where, orderBy);
    	PagingSelectCommand pcmd=cmd.createPagingSelectCommand(dataContext.getCriterias(),start,limit);
    	pcmd.setParams(params);
    	ListWrapper<M> ret= dataContext.listEntities(entryClass,pcmd);
    	return ret;
	}
	
    public ListWrapper<M> selectPaging(PagingSelectCommand pcmd) throws CommandExecuteExecption{
    	ListWrapper<M> ret=dataContext.listEntities(entryClass,pcmd);
    	return ret;
	}
	

	public abstract M newEntity() throws Exception;
}
