package lava.rt.linq;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


import lava.rt.common.SqlCommon;

public abstract class DataContext {

	private Connection connection;
	
	public DataContext(Connection connection) {
		this.connection=connection;
	}
	
	public  <M> Table<M>  createTable(Class<M> cls,String pkName){
		return new Table<M>(this, cls, pkName);
	}
	
	public  <M> View<M>  createView(Class<M> cls){
		return new View<M>(this, cls);
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
	
	
	protected int executeUpdate(String sql) throws SQLException{
		return 0;
	} 
	
	
	
}
