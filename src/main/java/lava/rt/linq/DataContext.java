package lava.rt.linq;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lava.rt.common.SqlCommon;

public abstract class DataContext {

	private Connection connection;
	
	public DataContext(Connection connection) {
		this.connection=connection;
	}
	
	protected final Map<String,String> SQL_CACHE=new HashMap<String,String>();
	
	public  <M> Table<M>  createTable(Class<M> cls,String tableName,String pkName){
		return new Table<M>(this, cls,tableName, pkName);
	}
	
	public  <M> View<M>  createView(Class<M> cls,String tableName){
		return new View<M>(this, cls,tableName);
	};
	
	
	
	
	
	protected  <M> int insert(M...m) throws SQLException {
		return SqlCommon.insert(connection, m);
	}
	
	
	protected <M>  List<M> executeQueryList(String sql) throws SQLException{
		
		return null;
	} 
	
    protected Object[][] executeQueryArray(String sql) throws SQLException{
		
		return null;
	} 
	
	
	protected int executeUpdate(String sql,Object[][] param) throws SQLException{
		return 0;
	} 
	
	
	
}
