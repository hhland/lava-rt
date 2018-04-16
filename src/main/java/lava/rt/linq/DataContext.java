package lava.rt.linq;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lava.rt.common.ReflectCommon;
import lava.rt.common.SqlCommon;
import lava.rt.instance.MethodInstance;

public abstract class DataContext {

	
	protected abstract Class thisClass() ;
	
	public DataContext(DataSource dataSource) {
		this.dataSource=dataSource;
	}
	
	private DataSource dataSource;
	
	
	protected final Map<String,String> SQL_CACHE=new HashMap<String,String>();
	
	public  <M> Table<M>  createTable(Class<M> cls,String tableName,String pkName){
		Table<M> table=null;
		try {
			table= new Table<M>(this, cls,tableName, pkName);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return table;
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
	
	
	
	
	
	
	protected <M>  List<M> executeQueryList(String sql,Class<M> cls) throws SQLException{
		Connection connection=this.dataSource.getConnection();
		List<M> list=new ArrayList<M>();
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		
		ResultSet resultSet=preparedStatement.executeQuery(sql);
		ResultSetMetaData metaData=resultSet.getMetaData();
		Map<String,Integer> meteDataMap=new HashMap<String,Integer>();
		for(int i=1;i<=metaData.getColumnCount();i++) {
			String key=metaData.getColumnName(i).toUpperCase();
			meteDataMap.put(key, i);
		}
		
		Map<String,Field> fieldMap=ReflectCommon.getFields(cls);
		fieldMap.forEach((k,v)->v.setAccessible(true));
		while(resultSet.next()) {
			M m=null;
			try {
				m = ReflectCommon.newInstance(cls);
			} catch (Exception e) {
			} 
			if(m==null) {
				throw new SQLException(cls.getName()+ " can't be instance");
			}
			int c=0;
			for(Iterator<String> it=fieldMap.keySet().iterator();c<metaData.getColumnCount()&&it.hasNext();) {
				String columnName=it.next().toUpperCase();
				if(!meteDataMap.containsKey(columnName))continue;
				int columnIndex=meteDataMap.get(columnName);
				Field field=fieldMap.get(columnName);
				try {
					field.set(m, resultSet.getObject(columnIndex));
				} catch (Exception e) {continue;}
				c++;
			}
			
			list.add(m);
		}
		MethodInstance.close.invoke(resultSet,preparedStatement,connection);  
		
		return list;
	} 
	
    protected Object[][] executeQueryArray(String sql) throws SQLException{
    	Connection connection=this.dataSource.getConnection();
		List<Object[]> list=new ArrayList<Object[]>();
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		
		ResultSet resultSet=preparedStatement.executeQuery(sql);
		ResultSetMetaData metaData=resultSet.getMetaData();
		int cc=metaData.getColumnCount();
		while(resultSet.next()) {
			Object[] objects=new Object[cc];
			for(int i=0;i<cc;i++) {
				objects[i]=resultSet.getObject(i+1);
			}
			list.add(objects);
		}
		MethodInstance.close.invoke(resultSet,preparedStatement,connection);  
		return list.toArray(new Object[list.size()][cc]);
	} 
	
	
	protected int executeUpdate(String sql,Object[][] params) throws SQLException{
		Connection connection=this.dataSource.getConnection();
		int re=0;
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		
		for(Object[] param :params) {
			
			for(int i=0;i<param.length;i++) {
				preparedStatement.setObject(i+1, param[i]);
			}
			preparedStatement.addBatch();
		}
		int[] res= preparedStatement.executeBatch();
		MethodInstance.close.invoke(preparedStatement,connection);
		for(int r:res)re+=r;
		return re;
	} 
	
	protected int[]  executeInsert(String sql,Object[][] params) throws SQLException{
		Connection connection=this.dataSource.getConnection();
		int[] pks=new int[params.length];
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		
		//for(Object[] param :params) {
		for(int j=0;j<params.length;j++) {
			Object[] param=params[j];
			for(int i=0;i<param.length;i++) {
				preparedStatement.setObject(i+1, param[i]);
			}
			preparedStatement.executeUpdate();
			ResultSet resultSet= preparedStatement.getGeneratedKeys();
			if(resultSet.next()) {
				pks[j]=resultSet.getInt(1);
			}
			resultSet.close();
		}
		MethodInstance.close.invoke(preparedStatement,connection);
		//for(int r:res)re+=r;
		return pks;
	} 
	
	
}
