package lava.rt.linq.src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OracleDataContextSrcGener extends DataContextSrcGener{

	public OracleDataContextSrcGener(Connection connection) {
		super(connection);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<String> loadViews(String databaseName) throws SQLException {
		// TODO Auto-generated method stub
		Set<String> tables=new HashSet<String>();
		String sql="select VIEW_NAME from user_views";
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
		String sql="select cu.table_name,cu.column_name from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' ";
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		
		ResultSet resultSet=preparedStatement.executeQuery();
		while(resultSet.next()) {
		   String table=resultSet.getString(1).toUpperCase();
		   String pkName=resultSet.getString(2).toUpperCase();
		   if(table.startsWith("BIN$"))continue;
		   tablePks.put(table, pkName);
		}
		return tablePks;
	}

	@Override
	protected Map<String, List<ProcedureParamSrc>> loadProcedures(String databaseName) {
		// TODO Auto-generated method stub
		return new HashMap<>();
	}

	
	@Override
	protected Class<? extends DataContextSrcGener> thisClass() {
		// TODO Auto-generated method stub
		return this.getClass();
	}
	
	
}
