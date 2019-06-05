package lava.rt.linq;



import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import javax.sql.DataSource;

import lava.rt.common.ReflectCommon;
import lava.rt.common.SqlCommon;



public abstract class DataContext {

	public static boolean DEBUG=false;
	
	protected abstract Class thisClass() ;
	
	private final  Map<String,Field> fieldMap;
	
	private final Map<Class,Table> tableMap=new HashMap<>();
	
	private final Map<Class,View>  viewMap=new HashMap<>();

	public static Logger LOGGER=new Logger() {
		
		public void log(Class cls,String msg) {
			   ;
		}
		
		public void log(Class cls,Exception ex) {
			   ;
		}
	};

	protected DataContext() {
		fieldMap=ReflectCommon.getDeclaredFields(thisClass());
	}
	
	public DataContext(DataSource dataSource) {
		fieldMap=ReflectCommon.getDeclaredFields(thisClass());
		this.dataSource=dataSource;
	}
	
	private DataSource dataSource;
	

	
	
	protected final Map<String,String> sqlMap=new HashMap<String,String>();
	
	
	
	public  <M extends Entry> Table<M>  createTable(Class<M> cls,String tableName,String pkName){
		Table<M> table=new Table<M>(this, cls,tableName, pkName);
		tableMap.put(cls, table);
		return table;
	}
	
	public  <M extends Entry> View<M>  createView(Class<M> cls,String tableName){
		View<M> view=new View<M>(this, cls,tableName);
		viewMap.put(cls, view);
		return view; 
	};
	
	
	
	public <M extends Entry> Table<M>  getTable(Class<M> mcls){
	      Table<M> ret=(Table<M>)tableMap.get(mcls);
	      return ret;
	}
	
	public  <M extends Entry> View<M>  getView(Class<M> mcls){
		View<M> ret=(View<M>)viewMap.get(mcls);
	      return ret;
	};
	
	
	
	
	
	
	
	public <M extends Entry>  List<M> executeQueryList(String sql,Class<M> cls,Object...params) throws SQLException{
		Connection connection=getConnection();
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
		
		View<M> view=getTable(cls);
		if(view==null) {
			view=getView(cls);
		}
		while(resultSet.next()) {
			M m = newEntry(cls);
			
			if(m==null) {
				throw new SQLException(cls.getName()+ " can't be instance");
			}
			int c=0;
			for(Iterator<String> it=view.fieldMap.keySet().iterator();c<metaData.getColumnCount()&&it.hasNext();) {
				String columnName=it.next().toUpperCase();
				if(!meteDataMap.containsKey(columnName))continue;
				int columnIndex=meteDataMap.get(columnName);
				Field field=view.fieldMap.get(columnName);
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
	
    public Object[][] executeQueryArray(String sql,Object...params) throws SQLException{
    	Connection connection=getConnection();
    	Object[][] re=SqlCommon.executeQueryArray(connection, sql, params);
		ReflectCommon.close(connection);  
		return re;
	} 
	
    
	
    public int executeUpdate(String sql,Object... param) throws SQLException{
		Connection connection=getConnection();
		int re=0;
		re+=SqlCommon.executeUpdate(connection, sql, param);
		return re;
	} 
	
	public int  executeInsertReturnPk(String sql,Object... param) throws SQLException{
		int pk=0;
		Connection connection=getConnection();
		
		PreparedStatement preparedStatement= connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			
		
		for(int i=0;i<param.length;i++) {
				preparedStatement.setObject(i+1, param[i]);
		}
		preparedStatement.executeUpdate();
		ResultSet resultSet= preparedStatement.getGeneratedKeys();
		if(resultSet.next()) {
				pk=resultSet.getInt(1);
		}
		ReflectCommon.close(resultSet,preparedStatement);
		return pk;
	} 
	

	public int  executeBatch(String sql,Object[]...params) throws SQLException{
		int ret=0;
		Connection connection=getConnection();    
	    connection.setAutoCommit(false);
		ret= SqlCommon.executeBatch(connection, sql, params);
		
		return ret;
	} 
	
	
	public <M extends Entry> int insert(M...entrys) throws SQLException{
		int re=0;
		if(entrys.length==0)return re;
		for(M entry:entrys) {
			Class cls=entry.getClass();
			Table table= this.getTable(cls);
			re+= table.insert(entry);
		}
		return re;
	}
	
	public <M extends Entry> int update(M...entrys) throws SQLException{
		int re=0;
		if(entrys.length==0)return re;
		for(M entry:entrys) {
			Class cls=entry.getClass();
			Table table= this.getTable(cls);
			re+= table.update(entry);
		}
		
		
		return re;
	}
	
	public <M extends Entry> int delete(M...entrys) throws SQLException{
		int re=0;
		if(entrys.length==0)return re;
		for(M entry:entrys) {
			Class cls=entry.getClass();
			Table table= this.getTable(cls);
			re+= table.delete(entry);
		}
		return re;
	}
	
	public Connection getConnection() throws SQLException {
		Connection ret=this.dataSource.getConnection();
		return ret;
	}
	
	protected <E extends Entry> E newEntry(Class<E> entryClass)  {
		E ret=null;
		try {
		
			ret=(E)entryClass.getConstructors()[0].newInstance(this);
		} catch (Exception e) {
			e.printStackTrace();
			
		} 
		return ret;
	}
	
     protected Object[][] callProcedure(String procName, Object... params) throws SQLException {
	
			return SqlCommon.callProcedure(getConnection(), procName, params);
		     
	 }
	
     
	
	
	public interface Logger{
		
		 void log(Class cls,String msg);
		
		 void log(Class cls,Exception ex);
		
	}
}
