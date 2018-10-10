package lava.rt.linq.src;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
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

import javax.sql.DataSource;


import lava.rt.common.TextCommon;
import lava.rt.linq.Column;
import lava.rt.linq.DataContext;
import lava.rt.linq.Entry;
import lava.rt.linq.Table;
import lava.rt.linq.View;

public abstract class DataContextSrcGener   {

	public static String COLUMN_METHOD="		public String eq(Object val) {return sql_eq(true,name(),val);}\r\n" + 
			"		public String notEq(Object val) {return sql_eq(false,name(),val);}\r\n" + 
			"		public String lt(Object val) {return sql_lt(true,name(),val);}\r\n" + 
			"		public String notLt(Object val) {return sql_lt(false,name(),val);}\r\n" + 
			"		public String gt(Object val) {return sql_gt(true,name(),val);}\r\n" + 
			"		public String notGt(Object val) {return sql_gt(false,name(),val);}\r\n" + 
			"		public String isNull() {return sql_isnull(true,name());}\r\n" + 
			"		public String isNotNull() {return sql_isnull(false,name());}\r\n" + 
			"		public <T> String in(T... vals) {return sql_in(true,name(),vals);}\r\n" + 
			"		public <T> String notIn(T...vals) {return sql_in(false,name(),vals);}\r\n" + 
			"		public <T> String between(T from,T to) {return sql_between(true,name(),from,to);}\r\n" + 
			"		public String like(String val) {return sql_like(true, name(),val);}\r\n" + 
			"		public String notLike(String val) {return sql_like(false, name(),val);}";
	
	protected Connection connection;
	
	protected Set<String> columnNames=new HashSet<>();
	
	public DataContextSrcGener(Connection connection) {
		this.connection=connection;
	}
	
	public String toSrc(Class<? extends DataContext> cls,String databaseName,String...justTables) throws SQLException {
		StringBuffer src=new StringBuffer("");
		
		src.append("package "+cls.getPackage().getName()+"; \n\n");
		
		for(ColumnStruct columnStruct : ColumnStruct.values()) {
			String fieldClsName=columnStruct.fieldCls.getName();
			if(fieldClsName.startsWith("[L")) {
				fieldClsName=fieldClsName.substring(2,fieldClsName.length()-1);
			}
			src.append("import "+fieldClsName+"; \n");
		}
		src.append("import "+Table.class.getName()+"; \n")
		.append("import "+View.class.getName()+"; \n")
		.append("import " + Column.class.getName()+" ; \n")
		.append("import "+ Entry.class.getName()+"; \n")
		
        
		.append("import "+DataSource.class.getName()+"; \n\n\n")
		
		;
		
		src.append("public class "+cls.getSimpleName()+" extends "+DataContext.class.getName()+"{ \n\n");
		
		src.append("\t@Override\r\n" + 
				"\tprotected Class thisClass() {return this.getClass(); }\n\n")
		.append("\t public "+cls.getSimpleName()+"("+DataSource.class.getSimpleName()+" dataSource){ super(dataSource);  } \n\n");
		
		Set<String> tables=new HashSet<>(),views=loadViews(databaseName);
		
		Map<String, String> tablesPks=loadTablesPks(databaseName);
		
		if(justTables.length>0) {
			for(String justTable:justTables) {
				tables.add(justTable);
			}
		}else {
			tables=tablesPks.keySet();
		}
		
		
		
		for(String table:tables) {
			
			String pkName="ID";
			
			String tn=table.toUpperCase();
			if(views.contains(tn))continue;
			
			if(tablesPks.containsKey(tn)) {
				pkName=tablesPks.get(tn);
			}
			src.append("\t public final Table<"+tn+"> table"+tn+"=createTable("+tn+".class,\""+table+"\",\""+pkName+"\");\n");
		}
		
		src.append("\n\n");
		
		for(String table:views) {
			
			
			String tn=table.toUpperCase();
			
			src.append("\t public final View<"+tn+"> view"+tn+"=createView("+tn+".class,\""+table+"\");\n");
		}
		
		src.append("\n\n");
		
		tables.addAll(views);
		for(String table:tables) {
			String pkName=null;
			String tn=table.toUpperCase();
			if(tablesPks.containsKey(tn)) {
				pkName=tablesPks.get(tn);
			}
			TableSrc tableSrc=new TableSrc(tn,pkName);
			src.append(tableSrc.toSrc());
		}
		
		src
		.append("\tpublic final static Criteria CRITERIA=new Criteria();\n\n")
		.append("\tpublic  static class Criteria{ \n\n")
		.append("\t\tprivate Criteria() {} \n\n")
		.append("\t\tpublic static final Column \n");
		
		for(String colName:columnNames) {
			src.append("\t\t"+ colName+" = new Column(\""+colName+"\"),\n" );
		}
		src.deleteCharAt(src.length()-2);
		src.append( "\t\t;\n\n")
		.append("\t} \n\n")
		;
		src.append("\n\n\n} //end");
		
		return src.toString();
	}
	
	
	
	
	public abstract Set<String> loadViews(String databaseName) throws SQLException;
	
	
	public abstract Map<String,String> loadTablesPks(String databaseName) throws SQLException;
	
	
	
	
	public class TableSrc{
		
		public String tableName,pkName;
		
		public TableSrc(String tableName,String pkName) {
			this.tableName=tableName;
			this.pkName=pkName;
		}
		
		public StringBuffer toSrc() throws SQLException{
	        StringBuffer context=new StringBuffer("");
	    	
	        //context.append("\t public    class "+tableName+"Table extend Table<"+tableName+"> { \n\n ")
	        //.append("\t\tpublic "+tableName+"Table (DataContext dataContext){ super(dataContext, "+tableName+".class, \""+tableName+"\", \""+pkName+"\") }\n\n")
	        
	        
	        //.append("\t\tpublic final  Column "+genColsEnum() +"; \n\n")
	        //.append("\t } \n\n");
	        
	        context
	       
	    	.append("\t public  class "+tableName+" extends ")
	    	.append(""+Entry.class.getSimpleName())
	    	
	    	.append(" {\n\n")
	    	//.append("\t\t private static final long serialVersionUID = 1L; ")
	    	.append("\n\n")
	    	.append(this.genColsSrc())
	    	.append("\n\n")
	    	.append(genOveriderSrc())
	    	.append("\n\n")
	    	.append("\t } //end "+tableName)
	        .append("\n\n")
	        ;
	        return context;
	    }
		
		private String genOveriderSrc() {
			StringBuffer src=new StringBuffer("");
			src.append("\t\t@"+Override.class.getSimpleName()+"\n")
			.append("\t\tpublic boolean equals(Object obj) {return this.toString().equals(obj.toString());} \n\n")
		    ;
			if(pkName!=null) {
			src.append("\t\t@"+Override.class.getSimpleName()+"\n")
			.append("\t\tpublic String toString() {return this.getClass().getName()+\":"+tableName+":\"+this.getPk();}")
			.append("\n\n")
			.append("\t\t@"+Override.class.getSimpleName()+"\n")
			.append("\t\tpublic String getPk() {return this."+pkName+";}")
			
			.append("\n\n")
			.append("\t\t@"+Override.class.getSimpleName()+"\n")
			.append("\t\tpublic Class<? extends Entry> thisClass() {return this.getClass() ;}")
			;}
			return src.toString();
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
				columnNames.add(colName);
		    	int colType=resultSetMetaData.getColumnType(i);
		    	Class colClass=ColumnStruct.toClass(colType);
		        String colClsName=colClass.getSimpleName();
		        
		        sbFields.append("\t\t private " +colClsName+ " "+colName+ " ; \n " );
		        
		        sbGetSeter.append("\t\t public "+colClsName+" get"+colName+"(){ return this."+colName+"; } \n")
		        .append("\t\t public void set"+colName+"("+colClsName+" "+ colName +" ){  this."+colName+"="+colName+"; } \n")
		        ;
			}
			close(preparedStatement,resultSetMetaData);
			return sbFields+"\n"+sbGetSeter;
		}
		
		
		private String genColsEnum() throws SQLException {
			// TODO Auto-generated method stub
			StringBuffer sbFields=new StringBuffer();
			String sql=MessageFormat.format("select * from {0} where 1=2",tableName );
			PreparedStatement preparedStatement= connection.prepareStatement(sql);
			ResultSetMetaData resultSetMetaData= preparedStatement.executeQuery().getMetaData();
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				String colName=resultSetMetaData.getColumnName(i)
						.trim().toUpperCase().replace(" ", "_");
		    	int colType=resultSetMetaData.getColumnType(i);
		    	Class colClass=ColumnStruct.toClass(colType);
		        String colClsName=colClass.getSimpleName();
		        
		        sbFields.append("\t\t "+colName+" =new Column(\""+colName+"\"),\n");
		        
		        
			}
			
			close(preparedStatement,resultSetMetaData);
			return TextCommon.trim(sbFields.toString(),",");
		}
	}
	
	
	
	public enum ColumnStruct{
		STRING(String.class,Types.VARCHAR,Types.CHAR,Types.NVARCHAR,Types.LONGNVARCHAR,Types.NCHAR,Types.LONGVARCHAR)
		,INT(Integer.class,Types.INTEGER,Types.SMALLINT,Types.BIGINT,Types.TINYINT)
		,FLOAT(Float.class,Types.FLOAT)
		,DOUBLE(Double.class,Types.DOUBLE)
		,DATE(Date.class,Types.DATE,Types.TIME,Types.TIMESTAMP)
		,DECIMAL(BigDecimal.class,Types.DECIMAL,Types.NUMERIC)
		,BIN(Byte.class,Types.LONGVARBINARY,Types.BIT)
		,BOOLEAN(Boolean.class,Types.BOOLEAN)
		,BITS(Byte[].class,Types.BLOB)
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
			if(cls==null) {
				cls=cls;
			}
			return cls;
		}
	}
	
	
	public static DataContextSrcGener create(Connection connection,String url) throws SQLException {
	    DataContextSrcGener ret=null;
		
		if(url.startsWith("jdbc:mysql:")) {
			ret=new MySQLDataContextSrcGener(connection);
		}else if(url.startsWith("jdbc:sqlserver:")) {
			ret=new MSSQLServerDataContextSrcGener(connection);
		}
	    return ret;
	}
	
	protected int close(Object... objs) {
		int ret=0;
		for(Object obj:objs) {
			try {
				obj.getClass().getMethod("close").invoke(obj);
				ret++;
			} catch (Exception e) {}
		}
		return ret;
	}
	
}
