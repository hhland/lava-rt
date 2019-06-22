package lava.rt.common;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import lava.rt.sqlparser.SingleSqlParserFactory;
import lava.rt.sqlparser.SqlSegment;



public final class SqlCommon {

	public static int executeBatch(Connection connection,String sql,Object[]... params) throws SQLException{
		
		int re=0;
		try(PreparedStatement preparedStatement= connection.prepareStatement(sql);){
		
		for(Object[] param :params) {
			for(int i=0;i<param.length;i++) {
				preparedStatement.setObject(i+1, param[i]);
			}
			preparedStatement.addBatch();
			re++;
		}
		int[] res= preparedStatement.executeBatch();
		for(int i:res) {
			re+=i;
		}
		}
		
		return re;
	} 
	
    public static int executeBatch(Connection connection,String[] sqls) throws SQLException{
		
		int re=0;
		try(Statement statement= connection.createStatement();){
		
		for(String sql :sqls) {
			statement.addBatch(sql);
			re++;
		}
		int[] res= statement.executeBatch();
		for(int i:res) {
			re+=i;
		  }
		}
		
		return re;
	} 
	
      public static int executeUpdate(Connection connection,String sql,Object...params) throws SQLException{
		
		int re=0;
		try(PreparedStatement preparedStatement= connection.prepareStatement(sql);){
		
			for(int i=0;i<params.length;i++) {
				preparedStatement.setObject(i+1, params[i]);
			}
			
		re= preparedStatement.executeUpdate();
		}
		
		return re;
	} 
      
      
      
      
      
      public static Object[][] executeQueryArray(Connection connection,String sql,Object...params) throws SQLException{
    	int cc=0;
  		List<Object[]> list=new ArrayList<Object[]>();
  		try(PreparedStatement preparedStatement= connection.prepareStatement(sql)){
  		for(int i=0;i<params.length;i++) {
  			preparedStatement.setObject(i+1,params[i] );
  		}
  		 try(ResultSet resultSet=preparedStatement.executeQuery();){
  		ResultSetMetaData metaData=resultSet.getMetaData();
  		 cc=metaData.getColumnCount();
  		while(resultSet.next()) {
  			Object[] objects=new Object[cc];
  			for(int i=0;i<cc;i++) {
  				objects[i]=resultSet.getObject(i+1);
  			}
  			list.add(objects);
  		}
  		 }
  		}
  		  
		return list.toArray(new Object[list.size()][cc]);
  	} 
      
      
      public static List<Map<String, Object>> executeQueryListMap(Connection connection, String sql, Object... params) throws SQLException {

  		List<Map<String, Object>> list = new ArrayList<>();
  		try(
  		PreparedStatement preparedStatement = connection.prepareStatement(sql);){
  		for (int i = 0; i < params.length; i++) {
  			preparedStatement.setObject(i + 1, params[i]);
  		}
  		  try(ResultSet resultSet = preparedStatement.executeQuery();){
  		ResultSetMetaData metaData = resultSet.getMetaData();
  		int cc = metaData.getColumnCount();
  		Map<String, Object> rowMap = null;
  		while (resultSet.next()) {
  			rowMap = new HashMap<>();
  			for (int i = 0; i < cc; i++) {
  				rowMap.put(metaData.getColumnName(i + 1), resultSet.getObject(i + 1));
  			}
  			list.add(rowMap);
  		}
  		  }
  		}
  		
  		return list;
  	}
      
      
     
	
      
     
      
      
      
      
      
  
      
     
}
