package lava.rt.linq;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lava.rt.common.SqlCommon;

public abstract class DataContext {

	private Connection connection;
	
	public DataContext(Connection connection) {
		this.connection=connection;
	}
	
	public abstract <M> Table<M>  getTable(Class<M> cls);
	
	public abstract <M> View<M>  getView(Class<M> cls);
	
	
	
	
	
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
