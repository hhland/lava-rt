package lava.rt.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class SqlCommon {

	public static int executeBatch(Connection connection,String sql,Object[]... params) throws SQLException{
		
		int re=0;
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		
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
		ReflectCommon.close(preparedStatement);
		
		return re;
	} 
	
      public static int executeUpdate(Connection connection,String sql,Object...params) throws SQLException{
		
		int re=0;
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		
			for(int i=0;i<params.length;i++) {
				preparedStatement.setObject(i+1, params[i]);
			}
			
		re= preparedStatement.executeUpdate();
		ReflectCommon.close(preparedStatement);
		return re;
	} 
      
      
      public static Object[][] executeQueryArray(Connection connection,String sql,Object...params) throws SQLException{
      	
  		List<Object[]> list=new ArrayList<Object[]>();
  		PreparedStatement preparedStatement= connection.prepareStatement(sql);
  		for(int i=0;i<params.length;i++) {
  			preparedStatement.setObject(i+1,params[i] );
  		}
  		ResultSet resultSet=preparedStatement.executeQuery();
  		ResultSetMetaData metaData=resultSet.getMetaData();
  		int cc=metaData.getColumnCount();
  		while(resultSet.next()) {
  			Object[] objects=new Object[cc];
  			for(int i=0;i<cc;i++) {
  				objects[i]=resultSet.getObject(i+1);
  			}
  			list.add(objects);
  		}
  		ReflectCommon.close(resultSet,preparedStatement,connection);  
  		return list.toArray(new Object[list.size()][cc]);
  	} 
	
}
