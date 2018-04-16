package lava.rt.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lava.rt.instance.MethodInstance;

public class SqlCommon {

	public static float executeBatch(Connection connection,String sql,Object[][] params) throws SQLException{
		
		float re=0;
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		
		for(Object[] param :params) {
			for(int i=0;i<param.length;i++) {
				preparedStatement.setObject(i+1, param[i]);
			}
			preparedStatement.addBatch();
		}
		int[] res= preparedStatement.executeBatch();
		MethodInstance.close.invoke(preparedStatement);
		for(int r:res)re+=r;
		float prec=re/params.length;
		return prec;
	} 
	
      public static int executeUpdate(Connection connection,String sql,Object...params) throws SQLException{
		
		int re=0;
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		
			for(int i=0;i<params.length;i++) {
				preparedStatement.setObject(i+1, params[i]);
			}
			
		re= preparedStatement.executeUpdate();
		MethodInstance.close.invoke(preparedStatement);
		return re;
	} 
	
}
