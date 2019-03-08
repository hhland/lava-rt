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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lava.rt.sqlparser.SingleSqlParserFactory;
import lava.rt.sqlparser.SqlSegment;



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
      
      
      public static int executeUpdate(Collection collection,String sql) throws SQLException{
  		
  		int re=0;
  		
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
  		ReflectCommon.close(resultSet,preparedStatement);  
  		return list.toArray(new Object[list.size()][cc]);
  	} 
      
      
      public static List<Map<String, Object>> executeQueryListMap(Connection connection, String sql, Object... params) throws SQLException {

  		List<Map<String, Object>> list = new ArrayList<>();
  		PreparedStatement preparedStatement = connection.prepareStatement(sql);
  		for (int i = 0; i < params.length; i++) {
  			preparedStatement.setObject(i + 1, params[i]);
  		}
  		ResultSet resultSet = preparedStatement.executeQuery();
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
  		ReflectCommon.close(resultSet, preparedStatement);
  		return list;
  	}
	
      
     
      public static <E> List<E> select(Collection<E> collection,String sql) throws SQLException{
    		
    	    List<E> re=new ArrayList<E>(collection);
    		
    		return re;
    	  }
      
      
      
      protected static List<SqlSegment> parsedSql(String sql)
      {
          sql=sql.trim();
          sql=sql.toLowerCase();
          sql=sql.replaceAll("\\s{1,}", " ");
          sql=""+sql+" ENDOFSQL";
          //System.out.println(sql);
          return SingleSqlParserFactory.generateParser(sql).RetrunSqlSegments();
      }
      
      
     
      
      
      public static void main(String[] args) {
          // TODO Auto-generated method stub
         //String test="select  a from  b " +
             //    "\n"+"where      a=b";
         //test=test.replaceAll("\\s{1,}", " ");
         //System.out.println(test);
         //程序的入口
          String testSql="select c1,c2,c3     from    t1,t2 where condi3=3 "+"\n"+"    or condi4=5 order by o1,o2";
          
          List<SqlSegment> results=parsedSql(testSql);
          for(SqlSegment result:results) {
            System.out.println(result);
          }
         //List<SqlSegment> result=test.getParsedSqlList(testSql);//保存解析结果
      }
      
     
}
