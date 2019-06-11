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

public   class  View<M extends Entity> {

	protected final DataContext dataContext;
	
	protected final String tableName;
	protected final Class<M> entryClass;
	protected final Map<String,Field> entryFieldMap;
	
	protected final String sqlSelect;
	
	protected View (DataContext dataContext,Class<M> entryClass,String tableName) {
		this.dataContext=dataContext;
		this.tableName=tableName;
		this.entryClass=entryClass;
		this.entryFieldMap=ReflectCommon.theDeclaredFieldMap(entryClass);
		
		
		entryFieldMap.forEach((k,v)->v.setAccessible(true));
		
		sqlSelect="select * from "+tableName;
	}
	
	
	
	
    
    
    public List<M> select(String where,Object...params) throws SQLException{
    	
    	String sql=sqlSelect+" "+where;
		return dataContext.executeQueryList(entryClass,sql,params);
	}
    
	
	
	public int count(String column,String where,Object...params) throws SQLException{
		String pattern="select count({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	
    	return (int)dataContext.executeQueryArray(sql,params)[0][0];
	}
	
	public float sum(String column,String where,Object...params) throws SQLException{
		String pattern="select sum({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	
    	return (float)dataContext.executeQueryArray(sql,params)[0][0];
	}
	
	public <T> T min(String column,String where,Object...params) throws SQLException{
		String pattern="select min({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	
    	return (T)dataContext.executeQueryArray(sql,params)[0][0];
	}
	
	public <T> T max(String column,String where,Object...params) throws SQLException{
		String pattern="select max({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	
    	return (T)dataContext.executeQueryArray(sql,params)[0][0];
	}






	@Override
	public String toString() {
		return "View [tableName=" + tableName + ", entryClass=" + entryClass + "]";
	}






	
	
	
	
	
}
