package lava.rt.linq;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.PooledConnection;

import lava.rt.common.ReflectCommon;
import lava.rt.pool.ListPool;

public   class  View<M extends Entry> {

	protected final DataContext dataContext;
	
	protected final String tableName;
	protected final Class<M> entryClass;
	protected final Map<String,Field> fieldMap;
	
	
	
	protected View (DataContext dataContext,Class<M> entryClass,String tableName) {
		this.dataContext=dataContext;
		this.tableName=tableName;
		this.entryClass=entryClass;
		this.fieldMap=ReflectCommon.getDeclaredFields(entryClass);
		fieldMap.forEach((k,v)->v.setAccessible(true));
		
		
	}
	
	
	protected M newEntry( )  {
		M ret=null;
		try {
			ret=this.entryClass.getConstructor().newInstance();
		} catch (Exception e) {} 
		return ret;
	}
	
    
    
    public List<M> select(String where,Object...params) throws SQLException{
    	String pattern="select * from {0} ";
    	String sql=MessageFormat.format(pattern, this.tableName)+where;
    	if(dataContext.DEBUG) {
    		dataContext.LOGGER.log(this.entryClass, sql);
    	}
		return dataContext.executeQueryList(sql,this.entryClass,params);
	}
    
	
	
	public int count(String column,String where,Object...params) throws SQLException{
		String pattern="select count({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	if(dataContext.DEBUG) {
    		dataContext.LOGGER.log(this.entryClass, sql);
    	}
    	return (int)dataContext.executeQueryArray(sql,params)[0][0];
	}
	
	public float sum(String column,String where,Object...params) throws SQLException{
		String pattern="select sum({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	if(dataContext.DEBUG) {
    		dataContext.LOGGER.log(this.entryClass, sql);
    	}
    	return (float)dataContext.executeQueryArray(sql,params)[0][0];
	}
	
	public <T> T min(String column,String where,Object...params) throws SQLException{
		String pattern="select min({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	if(dataContext.DEBUG) {
    		dataContext.LOGGER.log(this.entryClass, sql);
    	}
    	return (T)dataContext.executeQueryArray(sql,params)[0][0];
	}
	
	public <T> T max(String column,String where,Object...params) throws SQLException{
		String pattern="select max({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	if(dataContext.DEBUG) {
    		dataContext.LOGGER.log(this.entryClass, sql);
    	}
    	return (T)dataContext.executeQueryArray(sql,params)[0][0];
	}






	@Override
	public String toString() {
		return tableName;
	}
	
	
	
	
}
