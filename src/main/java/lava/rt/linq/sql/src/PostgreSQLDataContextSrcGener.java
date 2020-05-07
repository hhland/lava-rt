package lava.rt.linq.sql.src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PostgreSQLDataContextSrcGener extends DataContextSrcGener {

	public PostgreSQLDataContextSrcGener(Connection connection) {
		super(connection);
		// TODO Auto-generated constructor stub
		this.connection=connection;
	}

	@Override
	protected Class<? extends DataContextSrcGener> thisClass() {
		// TODO Auto-generated method stub
		return this.getClass();
	}

	@Override
	protected Map<String, List<ProcedureParamSrc>> loadProcedures(String databaseName) throws SQLException {
		// TODO Auto-generated method stub
		return new HashMap<>();
	}

	@Override
	public Set<String> loadViews(String databaseName) throws SQLException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				Set<String> tables=new HashSet<String>();
				String sql="SELECT   viewname   FROM   pg_views  WHERE  schemaname ='public'";
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
		// TODO Auto-generated method stub
				Map<String,String> tablePks=new HashMap<String,String>();
				String sql="select pg_class.relname,pg_attribute.attname as colname,pg_type.typname as typename,pg_constraint.conname as pk_name from \r\n" + 
						"pg_constraint  inner join pg_class \r\n" + 
						"on pg_constraint.conrelid = pg_class.oid \r\n" + 
						"inner join pg_attribute on pg_attribute.attrelid = pg_class.oid \r\n" + 
						"and  pg_attribute.attnum = pg_constraint.conkey[1]\r\n" + 
						"inner join pg_type on pg_type.oid = pg_attribute.atttypid\r\n" + 
						"where  pg_constraint.contype='p' ";
				PreparedStatement preparedStatement= connection.prepareStatement(sql);
				ResultSet resultSet=preparedStatement.executeQuery();
				while(resultSet.next()) {
				   String table=resultSet.getString(1).toUpperCase();
				   String pkName=resultSet.getString(2).toUpperCase();
				   tablePks.put(table, pkName);
				}
				return tablePks;
	}

	@Override
	public Map<String, String[]> loadColumnMetas(String databaseName) throws SQLException {
		// TODO Auto-generated method stub
		String sql="SELECT relname as TABLE_NAME,a.attname as COLUMN_NAME,a.attlen as CHARACTER_MAXIMUM_LENGTH, a.attnotnull as IS_NULLABLE,col_description(a.attrelid,a.attnum) as COLUMN_COMMENT,a.atttypmod-4 as ATTTYPMOD\r\n" + 
				"FROM pg_class as c,pg_attribute as a inner join pg_type on pg_type.oid = a.atttypid\r\n" + 
				"where a.attrelid = c.oid and a.attnum>0";
		Map<String, String[]> ret=new HashMap<>();
		 try(PreparedStatement preparedStatement= connection.prepareStatement(sql);
	        		ResultSet resultSet=preparedStatement.executeQuery();){
			while(resultSet.next()) {
				String tableName=resultSet.getString("TABLE_NAME")
						,columnName=resultSet.getString("COLUMN_NAME")
						,dataLength=resultSet.getString("CHARACTER_MAXIMUM_LENGTH")
		                ,nullable=resultSet.getString("IS_NULLABLE")
		                ,comments=resultSet.getString("COLUMN_COMMENT")
		                ,ATTTYPMOD=resultSet.getString("ATTTYPMOD")
						;
				
				String key=tableName.toUpperCase()+":"+columnName.toUpperCase();
				nullable="t".equals(nullable)?"N":"Y";
				if("-1".equals(dataLength)) {
					dataLength=ATTTYPMOD;
				}
				ret.put(key, new String[] {dataLength,nullable,comments});
				
			}
	    }
		 return ret;
	}

}
