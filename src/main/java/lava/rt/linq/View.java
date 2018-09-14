package lava.rt.linq;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public   class View<M> {

	protected DataContext dataContext;
	
	protected String tableName;
	protected Class<M> classM;
	
	
	protected View (DataContext dataContext,Class<M> classM,String tableName) {
		this.dataContext=dataContext;
		this.tableName=tableName;
		this.classM=classM;
	}
	
	
	
    
    
    
    public List<M> select(String where,Object...params) throws SQLException{
    	String pattern="select * from {0} ";
    	String sql=MessageFormat.format(pattern, this.tableName)+where;
    	if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
		return dataContext.executeQueryList(sql,this.classM,params);
	}
    
	
	
	public int count(String column,String where,Object...params) throws SQLException{
		String pattern="select count({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
    	return (int)dataContext.executeQueryArray(sql,params)[0][0];
	}
	
	public float sum(String column,String where,Object...params) throws SQLException{
		String pattern="select sum({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
    	return (float)dataContext.executeQueryArray(sql,params)[0][0];
	}
	
	public <T> T min(String column,String where,Object...params) throws SQLException{
		String pattern="select min({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
    	return (T)dataContext.executeQueryArray(sql,params)[0][0];
	}
	
	public <T> T max(String column,String where,Object...params) throws SQLException{
		String pattern="select max({0}) from {1} ";
    	String sql=MessageFormat.format(pattern,column, this.tableName)+where;
    	if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
    	return (T)dataContext.executeQueryArray(sql,params)[0][0];
	}
	
	
	
	
}
