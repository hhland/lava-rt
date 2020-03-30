package lava.rt.linq.sql.src;

import java.sql.*;
import java.util.*;

import lava.rt.linq.sql.SqlDataContext.ColumnMeta;

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
				try(PreparedStatement preparedStatement= connection.prepareStatement(sql);
				ResultSet resultSet=preparedStatement.executeQuery();){
				while(resultSet.next()) {
				   String table=resultSet.getString(1).toUpperCase();
				   tables.add(table);
				}
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
		try(PreparedStatement preparedStatement= connection.prepareStatement(sql);
		ResultSet resultSet=preparedStatement.executeQuery();){
		while(resultSet.next()) {
		   String table=resultSet.getString(1).toUpperCase();
		   String pkName=resultSet.getString(2).toUpperCase();
		   tablePks.put(table, pkName);
		}
		}
		return tablePks;
	}

	
	
	
	@Override
	protected Map<String, List<ProcedureParamSrc>> loadProcedures(String databaseName) throws SQLException {
		// TODO Auto-generated method stub
		Map<String,List<ProcedureParamSrc>> ret=new HashMap<>();
		String sql=" select  \n" + 
				
				"(SELECT top(1) p.name FROM sys.procedures p where sp.object_id = p.object_id) as PROC_NAME,\n" + 
				"   PARAM_NAME = name,  \n" + 
				"   PARAM_TYPE   = type_name(user_type_id),  \n" + 
				"   PARAM_LEN   = max_length,  \n" + 
				"   PARAM_PREC   = case when type_name(system_type_id) = 'uniqueidentifier' \n" + 
				"              then precision  \n" + 
				"              else OdbcPrec(system_type_id, max_length, precision) end,  \n" + 
				"   PARAM_SCALE   = OdbcScale(system_type_id, scale),  \n" + 
				"   PARAM_ORDER  = parameter_id,  \n" + 
				"   PARAM_COLL   = convert(sysname, \n" + 
				"                   case when system_type_id in (35, 99, 167, 175, 231, 239)  \n" + 
				"                   then ServerProperty('collation') end)  \n" + 
				"   ,PARAM_IS_OUTPUT = sp.is_output\n" + 
				"  from sys.parameters sp order by object_id,parameter_id";
		try(PreparedStatement preparedStatement= connection.prepareStatement(sql);
		ResultSet resultSet=preparedStatement.executeQuery();){
		while(resultSet.next()) {
		   String name=resultSet.getString("PROC_NAME");
		   if(name==null)continue;
		   
		   String key=name;
		   if(!ret.containsKey(key)) {
			   ret.put(key, new ArrayList<DataContextSrcGener.ProcedureParamSrc>());
		   }
		   ProcedureParamSrc paramSrc=new ProcedureParamSrc();
		   paramSrc.isOutput=resultSet.getInt("PARAM_IS_OUTPUT")==1;
		   paramSrc.paramName=resultSet.getString("PARAM_NAME");
		   if("int".equals(resultSet.getString("PARAM_TYPE"))) {
			   paramSrc.sqlType=Types.INTEGER;
			   paramSrc.cls=Integer.class;
		   }else if("float".equals(resultSet.getString("PARAM_TYPE"))) {
			   paramSrc.sqlType=Types.FLOAT;
			   paramSrc.cls=Float.class;
		   }else {
			   paramSrc.sqlType=Types.VARCHAR;
			   paramSrc.cls=String.class;
		   }
		   
		   ret.get(key).add(paramSrc);
		   
		  
		  }
		}
		
		return ret;
	}

	@Override
	protected Class<? extends DataContextSrcGener> thisClass() {
		// TODO Auto-generated method stub
		return this.getClass();
	}

	@Override
	public Map<String, String[]> loadColumnMetas(String databaseName) throws SQLException {
		// TODO Auto-generated method stub
		String sql="SELECT [TableName] = [Tables].name ,[ColumnName] = [Columns].name ,\n" + 
				"        [Description] = [Properties].value,\n" + 
				"        [SystemTypeName] = [Types].name ,\n" + 
				"        [Precision] = [Columns].precision ,\n" + 
				"        [Scale] = [Columns].scale ,\n" + 
				"        [MaxLength] = [Columns].max_length ,\n" + 
				"        [IsNullable] = [Columns].is_nullable ,\n" + 
				"        [IsRowGUIDCol] = [Columns].is_rowguidcol ,\n" + 
				"        [IsIdentity] = [Columns].is_identity ,\n" + 
				"        [IsComputed] = [Columns].is_computed ,\n" + 
				"        [IsXmlDocument] = [Columns].is_xml_document \n" + 
				"FROM sys.tables AS [Tables]\n" + 
				"        INNER JOIN sys.columns AS [Columns] ON [Tables].object_id = [Columns].object_id\n" + 
				"        INNER JOIN sys.types AS [Types] ON [Columns].system_type_id = [Types].system_type_id\n" + 
				"                                           AND is_user_defined = 0\n" + 
				"                                           AND [Types].name <> 'sysname'\n" + 
				"        LEFT OUTER JOIN sys.extended_properties AS [Properties] ON [Properties].major_id = [Tables].object_id\n" + 
				"                                                              AND [Properties].minor_id = [Columns].column_id\n" + 
				"                                                              AND [Properties].name = 'MS_Description'\n" + 
				"ORDER BY [Columns].column_id";
				
		Map<String, String[]> ret=new HashMap<>();
		 try(PreparedStatement preparedStatement= connection.prepareStatement(sql);
	        		ResultSet resultSet=preparedStatement.executeQuery();){
			while(resultSet.next()) {
				String tableName=resultSet.getString("TableName")
						,columnName=resultSet.getString("ColumnName")
						,dataLength=resultSet.getString("MaxLength")
		                ,nullable=resultSet.getString("IsNullable")
		                ,comments=resultSet.getString("Description")
						;
				String key=tableName+":"+columnName;
				ret.put(key, new String[] {dataLength,nullable,comments});
				
			}
	    }
		
		return ret;
	}

	

}
