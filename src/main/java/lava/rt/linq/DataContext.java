package lava.rt.linq;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lava.rt.common.SqlCommon;

public abstract class DataContext {

	
	protected abstract Class thisClass() ;
	
	public DataContext(DataSource dataSource) {
		this.dataSource=dataSource;
	}
	
	private DataSource dataSource;
	
	
	protected final Map<String,String> SQL_CACHE=new HashMap<String,String>();
	
	public  <M> Table<M>  createTable(Class<M> cls,String tableName,String pkName){
		return new Table<M>(this, cls,tableName, pkName);
	}
	
	public  <M> View<M>  createView(Class<M> cls,String tableName){
		return new View<M>(this, cls,tableName);
	};
	
	public <M> Table<M>  getTable(Class<M> mcls){
	      Table<M> table=null;
		  String fieldName="table"+mcls.getSimpleName();
	      for(Class cls=thisClass();!Object.class.equals(cls);cls=cls.getSuperclass()) {
	    	  try {
				Field field=cls.getDeclaredField(fieldName);
				table=(Table<M>)field.get(this);
				break;
			} catch (Exception e) {
				continue;
			}
	    	  
	      }
	      return table;
	}
	
	public  <M> View<M>  getView(Class<M> mcls){
		View<M> table=null;
		  String fieldName="view"+mcls.getSimpleName();
	      for(Class cls=thisClass();!Object.class.equals(cls);cls=cls.getSuperclass()) {
	    	  try {
				Field field=cls.getDeclaredField(fieldName);
				table=(View<M>)field.get(this);
				break;
			} catch (Exception e) {
				continue;
			}
	    	  
	      }
	      return table;
	};
	
	
	
	protected  <M> int insert(M...m) throws SQLException {
		return SqlCommon.insert(this.dataSource.getConnection(), m);
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
