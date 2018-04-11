package lava.rt.linq;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lava.rt.instance.MethodInstance;

public final class DataContextSrcGener  {

	protected Connection connection;
	
	public DataContextSrcGener(Connection connection) {
		this.connection=connection;
	}
	
	public String toSrc(Class<? extends DataContext> cls,String pkName) throws SQLException {
		StringBuffer src=new StringBuffer("");
		src.append("package "+cls.getPackage().getName()+"; \n\n");
		
		for(ColumnStruct columnStruct : ColumnStruct.values()) {
			src.append("import "+columnStruct.fieldCls.getName()+"; \n");
		}
		src.append("import "+Table.class.getName()+"; \n")
		.append("import "+Connection.class.getName()+"; \n\n\n")
		;
		
		src.append("public class "+cls.getSimpleName()+" extends "+DataContext.class.getName()+"{ \n\n");
		
		
		src.append("\t public "+cls.getSimpleName()+"("+Connection.class.getSimpleName()+" conn){ super(conn);  } \n\n");
		
		Set<String> tables=loadTables();
		
		for(String table:tables) {
			String tn=table.toUpperCase();
			src.append("\t public final Table<"+tn+"> table"+tn+"=createTable("+tn+".class,\""+pkName+"\");\n");
		}
		src.append("\n\n");
		for(String table:tables) {
			TableSrc tableSrc=new TableSrc(table.toUpperCase(), "ID");
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
		   String table=resultSet.getString(1);
		   tables.add(table);
		}
		return tables;
	}
	
	
	
	public class TableSrc{
		
		public String tableName,pkName;
		
		public TableSrc(String tableName,String pkName) {
			this.tableName=tableName;
			this.pkName=pkName;
		}
		
		public StringBuffer toSrc() throws SQLException{
	        StringBuffer context=new StringBuffer("");
	    	
	        
	        context
	    	.append("\t public  class "+tableName+" {")
	    	.append("\n\n\n")
	    	.append(this.genColsSrc())
	    	
	    	.append("\t } //end "+tableName)
	        .append("\n\n\n")
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
				String colName=resultSetMetaData.getColumnName(i).toUpperCase();
		    	int colType=resultSetMetaData.getColumnType(i);
		        String colClsName=ColumnStruct.toClass(colType).getSimpleName();
		        
		        sbFields.append("\t\t private " +colClsName+ " "+colName+ " ; \n " );
		        
		        sbGetSeter.append("\t\t public "+colClsName+" get"+colName+"(){ return this."+colName+"; } \n")
		        .append("\t\t public void set"+colName+"("+colClsName+" "+ colName +" ){  this."+colName+"="+colName+"; } \n\n")
		        ;
			}
			MethodInstance.close.invoke(preparedStatement,resultSetMetaData);
			return sbFields+"\n"+sbGetSeter;
		}
		
	}
	
	
	
	public enum ColumnStruct{
		STRING(String.class,Types.VARCHAR,Types.CHAR,Types.NVARCHAR,Types.LONGNVARCHAR,Types.NCHAR)
		,INT(Integer.class,Types.INTEGER,Types.SMALLINT,Types.BIGINT,Types.TINYINT)
		,FLOAT(Float.class,Types.FLOAT)
		,DATE(Date.class,Types.DATE,Types.TIME,Types.TIMESTAMP)
		,DECIMAL(BigDecimal.class,Types.DECIMAL)
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
