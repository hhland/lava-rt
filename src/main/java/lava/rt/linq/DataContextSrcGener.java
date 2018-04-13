package lava.rt.linq;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lava.rt.instance.MethodInstance;

public final class DataContextSrcGener   {

	protected Connection connection;
	
	public DataContextSrcGener(Connection connection) {
		this.connection=connection;
	}
	
	public String toSrc(Class<? extends DataContext> cls,String databaseName) throws SQLException {
		StringBuffer src=new StringBuffer("");
		src.append("package "+cls.getPackage().getName()+"; \n\n");
		
		for(ColumnStruct columnStruct : ColumnStruct.values()) {
			src.append("import "+columnStruct.fieldCls.getName()+"; \n");
		}
		src.append("import "+Table.class.getName()+"; \n")
		.append("import "+View.class.getName()+"; \n")
		.append("import "+ Serializable.class.getName()+"; \n")
		.append("import "+ Cloneable.class.getName()+"; \n")

		.append("import "+Connection.class.getName()+"; \n\n\n")
		;
		
		src.append("public class "+cls.getSimpleName()+" extends "+DataContext.class.getName()+"{ \n\n");
		
		
		src.append("\t public "+cls.getSimpleName()+"("+Connection.class.getSimpleName()+" conn){ super(conn);  } \n\n");
		
		Set<String> tables=loadTables(),views=loadViews(databaseName);
		Map<String, String> tablesPks=loadTablesPks(databaseName);
		
		for(String table:tables) {
			
			String pkName="ID";
			
			String tn=table.toUpperCase();
			if(views.contains(tn))continue;
			
			if(tablesPks.containsKey(tn)) {
				pkName=tablesPks.get(tn);
			}
			src.append("\t public final Table<"+tn+"> table"+tn+"=createTable("+tn+".class,\""+pkName+"\");\n");
		}
		
		src.append("\n\n");
		
		for(String table:views) {
			String pkName="null";
			
			String tn=table.toUpperCase();
			if(tablesPks.containsKey(tn)) {
				pkName=tablesPks.get(tn);
			}
			src.append("\t public final View<"+tn+"> view"+tn+"=createView("+tn+".class);\n");
		}
		
		src.append("\n\n");
		
		tables.addAll(views);
		for(String table:tables) {
			
			String tn=table.toUpperCase();
			
			TableSrc tableSrc=new TableSrc(tn);
			src.append(tableSrc.toSrc());
		}
		
		src.append("\n\n\n} //end");
		
		return src.toString();
	}
	
	
	public Set<String> loadTables() throws SQLException{
		Set<String> tables=new HashSet<String>();
		String sql="show tables";
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		ResultSet resultSet=preparedStatement.executeQuery();
		while(resultSet.next()) {
		   String table=resultSet.getString(1).toUpperCase();
		   tables.add(table);
		}
		return tables;
	}
	
	public Set<String> loadViews(String databaseName) throws SQLException{
		Set<String> tables=new HashSet<String>();
		String sql="select table_name from information_schema.`VIEWS` where TABLE_SCHEMA='"+databaseName+"'";
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		ResultSet resultSet=preparedStatement.executeQuery();
		while(resultSet.next()) {
		   String table=resultSet.getString(1).toUpperCase();
		   tables.add(table);
		}
		return tables;
	}
	
	
	public Map<String,String> loadTablesPks(String databaseName) throws SQLException{
		Map<String,String> tablePks=new HashMap<String,String>();
		String sql="select table_name,column_name from information_schema.`COLUMNS` where TABLE_SCHEMA='"+databaseName+"' and COLUMN_KEY='PRI'";
		PreparedStatement preparedStatement= connection.prepareStatement(sql);
		ResultSet resultSet=preparedStatement.executeQuery();
		while(resultSet.next()) {
		   String table=resultSet.getString(1).toUpperCase();
		   String pkName=resultSet.getString(2).toUpperCase();
		   tablePks.put(table, pkName);
		}
		return tablePks;
	}
	
	
	public class TableSrc{
		
		public String tableName,pkName;
		
		public TableSrc(String tableName) {
			this.tableName=tableName;
			//this.pkName=pkName;
		}
		
		public StringBuffer toSrc() throws SQLException{
	        StringBuffer context=new StringBuffer("");
	    	
	        
	        context
	    	.append("\t public  class "+tableName+" implements ")
	    	.append(""+Serializable.class.getSimpleName())
	    	.append(","+Cloneable.class.getSimpleName())
	    	.append(" {\n")
	    	.append(this.genColsSrc())
	    	
	    	.append("\t } //end "+tableName)
	        .append("\n\n")
	        ;
	        return context;
	    }

		private String genColsSrc() throws SQLException {
			// TODO Auto-generated method stub
			StringBuffer sbFields=new StringBuffer(),sbGetSeter=new StringBuffer();
			String sql=MessageFormat.format("select * from {0} where 1=2",tableName );
			PreparedStatement preparedStatement= connection.prepareStatement(sql);
			ResultSetMetaData resultSetMetaData= preparedStatement.executeQuery().getMetaData();
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				String colName=resultSetMetaData.getColumnName(i)
						.trim().toUpperCase().replace(" ", "_");
		    	int colType=resultSetMetaData.getColumnType(i);
		        String colClsName=ColumnStruct.toClass(colType).getSimpleName();
		        
		        sbFields.append("\t\t private " +colClsName+ " "+colName+ " ; \n " );
		        
		        sbGetSeter.append("\t\t public "+colClsName+" get"+colName+"(){ return this."+colName+"; } \n")
		        .append("\t\t public void set"+colName+"("+colClsName+" "+ colName +" ){  this."+colName+"="+colName+"; } \n")
		        ;
			}
			MethodInstance.close.invoke(preparedStatement,resultSetMetaData);
			return sbFields+"\n"+sbGetSeter;
		}
		
	}
	
	
	
	public enum ColumnStruct{
		STRING(String.class,Types.VARCHAR,Types.CHAR,Types.NVARCHAR,Types.LONGNVARCHAR,Types.NCHAR,Types.LONGVARCHAR)
		,INT(Integer.class,Types.INTEGER,Types.SMALLINT,Types.BIGINT,Types.TINYINT)
		,FLOAT(Float.class,Types.FLOAT)
		,DATE(Date.class,Types.DATE,Types.TIME,Types.TIMESTAMP)
		,DECIMAL(BigDecimal.class,Types.DECIMAL)
		,BIN(Byte.class,Types.LONGVARBINARY,Types.BIT)
		;
		
		private Class fieldCls;
		public Set<Integer> sqlTypes;
		
		ColumnStruct(Class fieldCls,Integer...types) {
			this.fieldCls=fieldCls;
			this.sqlTypes=new HashSet<Integer>();
			for(int type:types) {
				sqlTypes.add(type);
			}
			
		}
		
		public static Class toClass(int type) {
			Class cls=null;
			for(ColumnStruct columnStruct : ColumnStruct.values()) {
				for(Iterator<Integer> it =columnStruct.sqlTypes.iterator();it.hasNext(); ) {
					int val=it.next();
					if(val==type) {
					  cls=columnStruct.fieldCls;
				 	  return cls;
					}
				}
			}
			return cls;
		}
	}
}
