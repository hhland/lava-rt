package lava.rt.common;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Clob;
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
      
      
     @SuppressWarnings("unchecked")
	public static <T> T getObject(Blob blob) throws IOException, SQLException, ClassNotFoundException {
    	 T ret=null;
    	try(
    			InputStream is=blob.getBinaryStream();                //获取二进制流对象
    			BufferedInputStream bis=new BufferedInputStream(is);    //带缓冲区的流对象
    			){
    		
			byte[] buff=new byte[(int) blob.length()];
			if(-1!=(bis.read(buff, 0, buff.length))){            //一次性全部读到buff中
				ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(buff));
				ret=(T)in.readObject();                   //读出对象
				
				
			}

    	}
    	 return ret;
     }
	
      
     
     
      
      
      
      
  
      
     
}
