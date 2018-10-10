package lava.rt.linq.src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MSSQLServerDataContextSrcGener extends DataContextSrcGener {

	public MSSQLServerDataContextSrcGener(Connection connection) {
		super(connection);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<String> loadViews(String databaseName) throws SQLException {
		// TODO Auto-generated method stub
		
				Set<String> tables=new HashSet<String>();
				String sql="select name from sysobjects  where type='V'";
				PreparedStatement preparedStatement= connection.prepareStatement(sql);
				ResultSet resultSet=preparedStatement.executeQuery();
				while(resultSet.next()) {
				   String table=resultSet.getString(1).toUpperCase();
				   tables.add(table);
				}
				return tables;
	}

	@Override
	public Map<String, String> loadTablesPks(String databaseName) throws SQLException {
		
		// TODO Auto-generated method stub
		Map<String,String> tablePks=new HashMap<String,String>();
		String sql=" SELECT   obj.name AS table_name,col.name AS column_name " + 
				"FROM    dbo.syscolumns col " + 
				"        inner JOIN dbo.sysobjects obj ON col.id = obj.id " + 
				"                                         AND obj.xtype = 'U' " + 
				"                                         AND obj.status >= 0 " + 
				
				"where    (SELECT   count(*)" + 
				"                           FROM     dbo.sysindexes si " + 
				"                                    INNER JOIN dbo.sysindexkeys sik ON si.id = sik.id " + 
				"                                                              AND si.indid = sik.indid " + 
				"                                    INNER JOIN dbo.syscolumns sc ON sc.id = sik.id " + 
				"                                                              AND sc.colid = sik.colid " + 
				"                                    INNER JOIN dbo.sysobjects so ON so.name = si.name " + 
				"                                                              AND so.xtype = 'PK' " + 
				"                           WHERE    sc.id = col.id" + 
				"                                    AND sc.colid = col.colid " + 
				") >0 ";
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		ResultSet resultSet=preparedStatement.executeQuery();
		while(resultSet.next()) {
		   String table=resultSet.getString(1).toUpperCase();
		   String pkName=resultSet.getString(2).toUpperCase();
		   tablePks.put(table, pkName);
		}
		return tablePks;
	}

}
