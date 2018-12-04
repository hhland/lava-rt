package lava.rt.linq;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.sql.DataSource;

import lava.rt.common.ReflectCommon;
import lava.rt.common.SqlCommon;


public abstract class DataContext {

	public static boolean DEBUG=false;
	
	protected abstract Class thisClass() ;
	

	public static Logger LOGGER=new Logger() {
		
		public void log(Class cls,String msg) {
			   ;
		}
		
		public void log(Class cls,Exception ex) {
			   ;
		}
	};

	protected DataContext() {
	}
	
	public DataContext(DataSource dataSource) {
		this.dataSource=dataSource;
	}
	
	private DataSource dataSource;
	

	
	
	protected final Map<String,String> SQL_CACHE=new HashMap<String,String>();
	
	
	
	public  <M> Table<M>  createTable(Class<M> cls,String tableName,String pkName){
		Table<M> table=null;
		
	    table= new Table<M>(this, cls,tableName, pkName);
		
		return table;
	}
	
	public  <M> View<M>  createView(Class<M> cls,String tableName){
		return new View<M>(this, cls,tableName);
	};
	
	public <M> Table<M>  getTable(Class<M> mcls)throws NullPointerException{
	      Table<M> table=null;
		  String fieldName="table"+mcls.getSimpleName();
	     
		  Field field=ReflectCommon.getDeclaredFields(thisClass()).get(fieldName); //cls.getDeclaredField(fieldName);
			try {
				table=(Table<M>)field.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				String msg=MessageFormat.format("field:{0} can't find in class:{1}",fieldName,thisClass().getName() );
				throw new NullPointerException(msg);
			}
	      return table;
	}
	
	public  <M> View<M>  getView(Class<M> mcls) throws NullPointerException{
		View<M> table=null;
		  String fieldName="view"+mcls.getSimpleName();
	     
			Field field=ReflectCommon.getDeclaredFields(thisClass()).get(fieldName); //cls.getDeclaredField(fieldName);
			try {
				table=(View<M>)field.get(this);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				String msg=MessageFormat.format("field:{0} can't find in class:{1}",fieldName,thisClass().getName() );
				throw new NullPointerException(msg);
			}
				
	      return table;
	};
	
	@SuppressWarnings("unchecked")
	protected <M> M newModel(Class<M> mcls) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException {
		M m=null;
		Constructor con=mcls.getConstructors()[0];
		con.setAccessible(true);
		m=(M)con.newInstance(this);
		return m;
	}
	
	
	
	
	protected <M>  List<M> executeQueryList(String sql,Class<M> cls,Object...params) throws SQLException{
		Connection connection=this.dataSource.getConnection();
		List<M> list=new ArrayList<M>();
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		for(int i=0;i<params.length;i++) {
			preparedStatement.setObject(i+1,params[i] );
		}
		
		ResultSet resultSet=preparedStatement.executeQuery();
		ResultSetMetaData metaData=resultSet.getMetaData();
		Map<String,Integer> meteDataMap=new HashMap<String,Integer>();
		for(int i=1;i<=metaData.getColumnCount();i++) {
			String key=metaData.getColumnName(i).toUpperCase();
			meteDataMap.put(key, i);
		}
		
		Map<String,Field> fieldMap=ReflectCommon.getDeclaredFields(cls);
		fieldMap.forEach((k,v)->v.setAccessible(true));
		while(resultSet.next()) {
			M m=null;
			try {
				m = newModel(cls); //ReflectCommon.newInstance(cls);
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
		ReflectCommon.close(resultSet,preparedStatement);  
		
		return list;
	} 
	
    protected Object[][] executeQueryArray(String sql,Object...params) throws SQLException{
    	Connection connection=this.dataSource.getConnection();
    	Object[][] re=SqlCommon.executeQueryArray(connection, sql, params);
		ReflectCommon.close(connection);  
		return re;
	} 
	
	
    protected int executeUpdate(String sql,Object... param) throws SQLException{
		Connection connection=this.dataSource.getConnection();
		
		int re=0;
		re+=SqlCommon.executeUpdate(connection, sql, param);
		
		ReflectCommon.close(connection);
		return re;
	} 
	
	protected int[]  executeInsertReturnPk(String sql,Object[][] params) throws SQLException{
		Connection connection=this.dataSource.getConnection();
		int[] pks=new int[params.length];
		PreparedStatement preparedStatement= connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
		
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
		ReflectCommon.close(preparedStatement,connection);
		//for(int r:res)re+=r;
		return pks;
	} 
	

	protected int  executeBatch(String sql,Object[]...params) throws SQLException{
		int ret=0;
		try(Connection connection=this.dataSource.getConnection()){
		
			ret= SqlCommon.executeBatch(connection, sql, params);
		}finally {}
		return ret;
	} 
	
	
	public <E > int insert(E...entrys) throws SQLException{
		int re=0;
		if(entrys.length==0)return re;
		Class<E> cls=(Class<E>)entrys[0].getClass();
		Table<E> table= this.getTable(cls);
	
		re= table.insert(entrys);
		
		return re;
	}
	
	public <E> int update(E...entrys) throws SQLException{
		int re=0;
		if(entrys.length==0)return re;
		Class<E> cls=(Class<E>)entrys[0].getClass();
		Table<E> table= this.getTable(cls);
		re= table.update(entrys);
		
		return re;
	}
	
	public <E > int delete(E...entrys) throws SQLException{
		int re=0;
		if(entrys.length==0)return re;
		Class<E> cls=(Class<E>)entrys[0].getClass();
		Table<E> table= this.getTable(cls);
		
		re= table.delete(entrys);
		
		return re;
	}
	
	public interface Logger{
		
		 void log(Class cls,String msg);
		
		 void log(Class cls,Exception ex);
		
	}
}
