package lava.rt.linq.src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.MessageFormat;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;


import lava.rt.common.TextCommon;
import lava.rt.linq.Column;
import lava.rt.linq.DataContext;
import lava.rt.linq.DataSourceContext;
import lava.rt.linq.Entity;
import lava.rt.linq.OutputParam;
import lava.rt.linq.Table;
import lava.rt.linq.View;



public abstract class DataContextSrcGener   {

	public  long serialVersionUID=1L;
	
	protected abstract Class<? extends DataContextSrcGener> thisClass();
	
	protected Connection connection;
	
	protected Set<String> columnNames=new HashSet<>();
	
	private SrcEvent srcEvent=new SrcEvent();
	
	public DataContextSrcGener(Connection connection) {
		this.connection=connection;
	}
	
	
	
	public void setSrcEvent(SrcEvent srcEvent) {
		this.srcEvent = srcEvent;
	}



	public void saveLocalSrcTo(File srcFile,Class cls,String databaseName,String...justTables) throws SQLException, IOException {
		
		String src=toLocalSrc(cls, databaseName, justTables);
		srcFile.delete();
		srcFile.createNewFile();
		srcFile.setWritable(true);
		try(FileWriter fw=new FileWriter(srcFile)){
			fw.write(src);
		}
	}
	
    public  void saveRpcIntfSrcTo(File srcIntf,Class clsIntf,String databaseName,String...justTables) throws SQLException, IOException {
		
    	String src=toRpcIntfSrc(clsIntf, databaseName, justTables);
    	srcIntf.delete();
    	srcIntf.createNewFile();
    	srcIntf.setWritable(true);
		try(FileWriter fw=new FileWriter(srcIntf)){
			fw.write(src);
		}
		
		
	}
    
     public  void saveRpcImplSrcTo(File srcImpl,Class clsIntf,Class clsImpl,String databaseName,String...justTables) throws SQLException, IOException {
		
    	 String src=toRpcImplSrc(clsIntf,clsImpl, databaseName, justTables);
		srcImpl.delete();
		srcImpl.createNewFile();
		srcImpl.setWritable(true);
		try(FileWriter fw=new FileWriter(srcImpl)){
			fw.write(src);
		}
	}
	
	public String toLocalSrc(Class cls,String databaseName,String...justTables) throws SQLException {
		StringBuffer src=new StringBuffer(getFileInfo(databaseName));
		if(cls.getPackage()!=null) {
		 src.append("package "+cls.getPackage().getName()+"; \n\n");
		}
		
		src.append("import "+DataContext.class.getPackage().getName()+".*; \n")
		
		
		.append("import "+ List.class.getPackage().getName()+".*; \n")
		.append("import "+ BigDecimal.class.getPackage().getName()+".*; \n")
		
		.append("import "+ SQLException.class.getPackage().getName()+".*; \n")
		.append("import "+DataSource.class.getPackage().getName()+".*; \n\n\n")
		.append("import "+Serializable.class.getPackage().getName()+".*; \n\n\n")
		;
		
		
		
		
		onClassSrcOutter(src,cls);
		
		
		src.append("public class "+cls.getSimpleName()+" extends "+DataSourceContext.class.getName()+"{ \n\n");
		
		
		onClassSrcInner(src,cls);
		
		
		src
		.append("\tDataSource[] dataSources;\r\n" + 
				"		\r\n" + 
				//"	 public "+cls.getSimpleName()+"(DataSource dataSource){ dataSources=new DataSource[] {dataSource}; } \r\n" + 
				"	 \r\n" + 
				"	 @Override\r\n" + 
				"		protected DataSource[] getDataSources() {\r\n" + 
				"			// TODO Auto-generated method stub\r\n" + 
				"			return dataSources;\r\n" + 
				"		}\n\n")
		
		.append("\tprivate static final long serialVersionUID="+serialVersionUID+";\n\n")
		.append("\t@Override\r\n" + 
				"\tprotected Class thisClass() {return this.getClass(); }\n\n")
		//.append("\t public "+cls.getSimpleName()+"("+DataSource.class.getSimpleName()+" dataSource)throws "+Exception.class.getSimpleName()+"{ this.dataSources=new DataSource[]{dataSource};  } \n\n")
		.append("\t public "+cls.getSimpleName()+"("+DataSource.class.getSimpleName()+"... dataSources)throws "+Exception.class.getSimpleName()+"{ this.dataSources=dataSources;  } \n\n")
		;
		
		Set<String> tables=new HashSet<>(),views=loadViews(databaseName);
		
		Map<String, List<ProcedureParamSrc>>procs=loadProcedures(databaseName);
		
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
			
			String tn=table.toUpperCase()
					,cn=toClassName(tn)
					;
			if(views.contains(tn))continue;
			
			if(tablesPks.containsKey(tn)) {
				pkName=tablesPks.get(tn);
			}
			src.append("\t public final Table<"+cn+"> "+tn+"=createTable("+cn+".class,\""+table+"\",\""+pkName+"\");\n");
		}
		
		src.append("\n\n");
		
		for(String table:views) {
			
			
			String tn=table.toUpperCase()
					,cn=toClassName(tn)
					;
			
			src.append("\t public final View<"+cn+"> "+tn+"=createView("+cn+".class,\""+table+"\");\n");
		}
		
		src.append("\n\n");
		
		//tables.addAll(views);
		for(String table:tables) {
			String pkName=null;
			String tn=table;
			if(tablesPks.containsKey(tn)) {
				pkName=tablesPks.get(tn);
			}
			TableSrc tableSrc=new TableSrc(tn,pkName);
			src.append(tableSrc.toSrc(cls));
		}
		
		for(String table:views) {
			
			String tn=table;
			TableSrc tableSrc=new TableSrc(tn,null);
			src.append(tableSrc.toSrc(cls));
		}
		
        for(java.util.Map.Entry<String, List<ProcedureParamSrc>> ent : procs.entrySet() ) {
			
			String tn=ent.getKey();
			ProcedureSrc tableSrc=new ProcedureSrc(tn,ent.getValue());
			src.append(tableSrc.toSrc());
		}
		
		src
		.append("\tpublic final static Criteria CRITERIA=new Criteria();\n\n")
		.append("\tpublic  static class Criteria{ \n\n")
		.append("\t\tprivate Criteria() {} \n\n")
		.append("\t\tpublic static final Column \n");
		
		for(String colName:columnNames) {
			src.append("\t\t"+ toPropName(colName)+" = new Column(\""+colName+"\"),\n" );
		}
		src.deleteCharAt(src.length()-2);
		src.append( "\t\t;\n\n")
		.append("\t} \n\n")
		;
		src.append("\n\n\n} //end");
		
		return src.toString();
	}
	
	
	private void onClassSrcInner(StringBuffer src,Class contextCls) {
		// TODO Auto-generated method stub
		src.append("//onClassSrcInner----start\n\n");
		srcEvent.onClassSrcInner(src,contextCls);
		src.append("\n\n//onClassSrcInner----end\n\n");
	}



	private void onClassSrcOutter(StringBuffer src,Class contextCls) {
		// TODO Auto-generated method stub
		src.append("//onClassSrcOutter----start\n\n");
		srcEvent.onClassSrcOutter(src,contextCls);
		src.append("\n");
	}



	public String toRpcIntfSrc(Class cls,String databaseName,String...justTables) throws SQLException {
		StringBuffer src=new StringBuffer(getFileInfo(databaseName));
		if(cls.getPackage()!=null) {
		 src.append("package "+cls.getPackage().getName()+"; \n\n");
		}
		
		src
		.append("import "+DataContext.class.getPackage().getName()+".*; \n")
		
		
		.append("import "+ List.class.getPackage().getName()+".*; \n")
		
		.append("import "+ SQLException.class.getPackage().getName()+".*; \n")
		.append("import "+BigDecimal.class.getPackage().getName()+".*; \n\n\n")
		.append("import "+Serializable.class.getPackage().getName()+".*; \n\n\n")
		;
		
		
		
		src.append("public interface "+cls.getSimpleName()+" extends "+DataContext.class.getName()+"{ \n\n");
		
		src
		.append("\tpublic static final long serialVersionUID="+serialVersionUID+";\n\n")
		//.append("\t@Override\r\n" + 
		//		"\tprotected Class thisClass() {return this.getClass(); }\n\n")
		//.append("\t public "+cls.getSimpleName()+"("+DataSource.class.getSimpleName()+"... dataSources)throws "+Exception.class.getSimpleName()+"{ super(dataSources);  } \n\n")
		;
		
		Set<String> tables=new HashSet<>(),views=loadViews(databaseName);
		
		Map<String, List<ProcedureParamSrc>>procs=loadProcedures(databaseName);
		
		Map<String, String> tablesPks=loadTablesPks(databaseName);
		
		if(justTables.length>0) {
			for(String justTable:justTables) {
				tables.add(justTable);
			}
		}else {
			tables=tablesPks.keySet();
		}
		
		
		
		
		
		src.append("\n\n");
		
		//tables.addAll(views);
		for(String table:tables) {
			String pkName=null;
			String tn=table;
			if(tablesPks.containsKey(tn)) {
				pkName=tablesPks.get(tn);
			}
			TableSrc tableSrc=new TableSrc(tn,pkName);
			src.append(tableSrc.toSrc(cls));
		}
		
		for(String table:views) {
			
			String tn=table;
			TableSrc tableSrc=new TableSrc(tn,null);
			src.append(tableSrc.toSrc(cls));
		}
		
        for(java.util.Map.Entry<String, List<ProcedureParamSrc>> ent : procs.entrySet() ) {
			
			String tn=ent.getKey();
			ProcedureSrc tableSrc=new ProcedureSrc(tn,ent.getValue());
			src.append(tableSrc.toIntfSrc());
		}
		
		src
		.append("\tpublic final static Criteria CRITERIA=new Criteria();\n\n")
		.append("\tpublic  static class Criteria{ \n\n")
		.append("\t\tprivate Criteria() {} \n\n")
		.append("\t\tpublic static final Column \n");
		
		for(String colName:columnNames) {
			src.append("\t\t"+ toPropName(colName)+" = new Column(\""+colName+"\"),\n" );
		}
		src.deleteCharAt(src.length()-2);
		src.append( "\t\t;\n\n")
		.append("\t} \n\n")
		;
		src.append("\n\n\n} //end");
		
		return src.toString();
	}
	
	
	
	public String toRpcImplSrc(Class intfCls,Class cls,String databaseName,String...justTables) throws SQLException {
		StringBuffer src=new StringBuffer(getFileInfo(databaseName));
		if(cls.getPackage()!=null) {
		 src.append("package "+cls.getPackage().getName()+"; \n\n");
		}
		
		
		
		
		src.append("import "+DataContext.class.getPackage().getName()+".*; \n")
		
		
		.append("import "+ List.class.getPackage().getName()+".*; \n")
		
		.append("import "+ SQLException.class.getPackage().getName()+".*; \n")
		.append("import "+DataSource.class.getPackage().getName()+".*; \n\n\n")
		//.append("import "+Serializable.class.getName()+"; \n\n\n")
		;
		
		
		
		onClassSrcOutter(src,cls);
		
		
		src.append("public class "+cls.getSimpleName()+" extends "+DataSourceContext.class.getName()+" implements "+intfCls.getName()+"{ \n\n");
		
		
		onClassSrcInner(src,cls);
		
		src
		//.append("\tprivate static final long serialVersionUID=1L;\n\n")
		.append("\t@Override\r\n" + 
				"\tprotected Class thisClass() {return this.getClass(); }\n\n")
		//.append("\t public "+cls.getSimpleName()+"("+DataSource.class.getSimpleName()+"... dataSources)throws "+Exception.class.getSimpleName()+"{ super(dataSources);  } \n\n")
		;
		
		Set<String> tables=new HashSet<>(),views=loadViews(databaseName);
		
		Map<String, List<ProcedureParamSrc>>procs=loadProcedures(databaseName);
		
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
			
			String tn=table.toUpperCase()
					,cn=toClassName(tn)
					;
			if(views.contains(tn))continue;
			
			if(tablesPks.containsKey(tn)) {
				pkName=tablesPks.get(tn);
			}
			src.append("\t public final Table<"+cn+"> "+tn+"=createTable("+cn+".class,\""+table+"\",\""+pkName+"\");\n");
		}
		
		src.append("\n\n");
		
		for(String table:views) {
			
			
			String tn=table.toUpperCase()
					,cn=toClassName(tn)
					;
			
			src.append("\t public final View<"+cn+"> "+tn+"=createView("+cn+".class,\""+table+"\");\n");
		}
		
		src.append("\n\n");
		
		
		
        for(java.util.Map.Entry<String, List<ProcedureParamSrc>> ent : procs.entrySet() ) {
			
			String tn=ent.getKey();
			ProcedureSrc tableSrc=new ProcedureSrc(tn,ent.getValue());
			src.append(tableSrc.toSrc());
		}
	
		
		src.append("\n\n\n} //end");
		
		return src.toString();
	}
	
	
	
	
	private String getFileInfo(String databaseName) {
		// TODO Auto-generated method stub
		return "/*\r\n" + 
				 
				" *@Database "+databaseName+"\r\n" + 
				" *@SrcGener "+thisClass().getName()+"\r\n" + 
				" *@CreateAt "+Calendar.getInstance().getTime()+"\r\n" + 
				"*/ \n";
	}

	

	protected abstract Map<String,List<ProcedureParamSrc>> loadProcedures(String databaseName) throws SQLException;

	public abstract Set<String> loadViews(String databaseName) throws SQLException;
	
	
	public abstract Map<String,String> loadTablesPks(String databaseName) throws SQLException;
	
	
	protected class ProcedureParamSrc{
		
		public Class cls;
		public String  paramName;
		
		public int sqlType;
		
		public boolean isOutput;
		
	}
	
	public class ProcedureSrc{

		
		public String  procName,className;
		List<ProcedureParamSrc> paramSrcs;
		
		public ProcedureSrc(String procName, List<ProcedureParamSrc> paramSrcs) {
			// TODO Auto-generated constructor stub
			this.procName=procName;
			this.className=DataContextSrcGener.toClassName(procName);
			this.paramSrcs=paramSrcs;
		}

		public String toIntfSrc() {
			// TODO Auto-generated method stub
			 //StringBuffer context=new StringBuffer("");
		    	
			 StringBuffer src=new StringBuffer("");
				String[] funParams=new String[paramSrcs.size()]
						,callParams=new String[paramSrcs.size()];
				for(int i=0;i<paramSrcs.size();i++) {
					ProcedureParamSrc paramSrc=paramSrcs.get(i);
					String paramName=paramSrc.paramName.replace("@", "");
					if(paramSrc.isOutput) {
						funParams[i]=OutputParam.class.getSimpleName()+"<" +paramSrc.cls.getSimpleName()+"> "+paramName;
					}else {
					   funParams[i]=paramSrc.cls.getSimpleName()+" "+paramName;
					}
					callParams[i]=","+paramName;
				}
				src
				//.append("\t\t@"+Override.class.getSimpleName()+"\n")
				.append("\t\tpublic Object[][] "+procName+"(")
				.append(String.join(",", funParams))
				.append(") throws SQLException; ")
				.append("\n");
		        
		        return src.toString();
		}
		
		public Object toSrc() {
			// TODO Auto-generated method stub
			 StringBuffer context=new StringBuffer("");
		    	
		        //context.append("\t public    class "+tableName+"Table extend Table<"+tableName+"> { \n\n ")
		        //.append("\t\tpublic "+tableName+"Table (DataContext dataContext){ super(dataContext, "+tableName+".class, \""+tableName+"\", \""+pkName+"\") }\n\n")
		        
		        
		        //.append("\t\tpublic final  Column "+genColsEnum() +"; \n\n")
		        //.append("\t } \n\n");
		        
		        context
		       
		    //	.append("\t public   "+className+" extends ")
		    //	.append(""+Procedure.class.getSimpleName())
		    	
		    //	.append(" {\n\n")
		    	//.append("\t\t private static final long serialVersionUID = 1L; ")
		   // 	.append("\n\n")
		  // 	.append("\public "+className+"(DataContext dataContext, String procName) {\r\n" + 
		  //  			"			super(dataContext, procName);\r\n" + 
		  //  			"			// TODO Auto-generated constructor stub\r\n" + 
		  //  			"		}")
		    	.append("\n\n")
		    	.append(genOveriderSrc())
		    	.append("\n\n")
		   // 	.append("\t } //end "+procName)
		        .append("\n\n")
		        ;
		        
		        return context;
		}
		
		

		private String genOveriderSrc() {
			// TODO Auto-generated method stub
			StringBuffer src=new StringBuffer("");
			String[] funParams=new String[paramSrcs.size()]
					,callParams=new String[paramSrcs.size()];
			for(int i=0;i<paramSrcs.size();i++) {
				ProcedureParamSrc paramSrc=paramSrcs.get(i);
				String paramName=paramSrc.paramName.replace("@", "");
				if(paramSrc.isOutput) {
					funParams[i]=OutputParam.class.getSimpleName()+"<" +paramSrc.cls.getSimpleName()+"> "+paramName;
				}else {
				   funParams[i]=paramSrc.cls.getSimpleName()+" "+paramName;
				}
				callParams[i]=","+paramName;
			}
			src
			//.append("\t\t@"+Override.class.getSimpleName()+"\n")
			.append("\t\tpublic Object[][] "+procName+"(")
			.append(String.join(",", funParams))
			.append(") throws SQLException {")
			.append("\n")
			//.append("\t\t\tList<"+Param.class.getSimpleName()+"> params=new ArrayList<>();")
			.append("\n")
			//.append(String.join(",", callParams))
			.append("\n")
			.append("\t\t\t return callProcedure(\""+procName+"\""+String.join("", callParams)+");")
			.append("\n")
			.append("\t\t} \n\n")
			.append("\n\n")
			
		    ;
			
			return src.toString();
		}
		
	}
	
	public class TableSrc{
		
		public String tableName,pkName,className;
		
		
		
		public TableSrc(String tableName,String pkName) {
			this.tableName=tableName;
			this.pkName=pkName;
			this.className=DataContextSrcGener.toClassName(tableName);
			
		}
		
		protected StringBuffer toSrc(Class dcClass) throws SQLException{
	        StringBuffer context=new StringBuffer("");
	    	
	        //context.append("\t public    class "+tableName+"Table extend Table<"+tableName+"> { \n\n ")
	        //.append("\t\tpublic "+tableName+"Table (DataContext dataContext){ super(dataContext, "+tableName+".class, \""+tableName+"\", \""+pkName+"\") }\n\n")
	        
	        
	        //.append("\t\tpublic final  Column "+genColsEnum() +"; \n\n")
	        //.append("\t } \n\n");
	        
	        context
	       
	    	.append("\t public  class "+className+" extends ")
	    	.append(""+Entity.class.getSimpleName())
	    	.append(" implements "+Serializable.class.getSimpleName())
	    	.append(" {\n\n")
	    	//.append("\t\t private static final long serialVersionUID = 1L; ")
	    	//.append("\t public "+className+"(){ super(); }")
	    	.append("\n\n")
	    	.append("\t\tprivate static final long serialVersionUID = "+dcClass.getSimpleName()+".serialVersionUID;")
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
			src
			//.append("\t\t@"+Override.class.getSimpleName()+"\n")
			//.append("\t\tpublic boolean equals(Object obj) {return this.toString().equals(obj.toString());} \n\n")
			.append("\n\n")
			.append("\t\t@"+Override.class.getSimpleName()+"\n")
			.append("\t\tpublic Class<? extends "+Entity.class.getSimpleName()+"> thisClass() {return this.getClass() ;}")
		    ;
			if(pkName!=null) {
			//src.append("\n\n").append("\t\t@"+Override.class.getSimpleName()+"\n")
			//.append("\t\tpublic String toString() {return this.getClass().getName()+\":"+tableName+":\"+this."+pkName+";}")
			//.append("\n\n")
			//.append("\t\t@"+Override.class.getSimpleName()+"\n")
			//.append("\t\tpublic Object getPk() {return this."+pkName+";}")
			
			
			;}
			return src.toString();
		}

		private String genColsSrc() throws SQLException {
			// TODO Auto-generated method stub
			StringBuffer sbFields=new StringBuffer(),sbGetSeter=new StringBuffer();
			String sql=MessageFormat.format("select * from {0} where 1=2",tableName );
			try(
			PreparedStatement preparedStatement= connection.prepareStatement(sql);
					ResultSet resultSet=preparedStatement.executeQuery();
			){
				ResultSetMetaData resultSetMetaData= resultSet.getMetaData();
			  for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
			
				String colName=resultSetMetaData.getColumnName(i)
						.trim().toUpperCase().replace(" ", "_");
				columnNames.add(colName);
		    	int colType=resultSetMetaData.getColumnType(i);
		    	Class colClass=ColumnStruct.toClass(colType);
		    	
		        String colClsName=colClass.getSimpleName();
		        String propName0=DataContextSrcGener.toClassName(colName),
				propName1=propName0.substring(0,1).toLowerCase()+propName0.substring(1);
		        
		        sbFields.append("\t\t private " +colClsName+ " "+colName+ " ; \n " );
		        
		        sbGetSeter.append("\t\t public "+colClsName+" get"+propName0+"(){ return this."+colName+"; } \n")
		        .append("\t\t public void set"+propName0+"("+colClsName+" "+ propName1 +" ){  this."+colName+"="+propName1+"; } \n")
		        ;
			}
			}catch(Exception ex) {}
			return sbFields+"\n"+sbGetSeter;
		}
		
		
		
	}
	
	public class SrcEvent{
		
		protected void onClassSrcOutter(StringBuffer src,Class contextCls) {
			// TODO Auto-generated method stub
			
		}

		protected void onClassSrcInner(StringBuffer src,Class contextCls) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public enum ColumnStruct{
		STRING(String.class,Types.VARCHAR,Types.CHAR,Types.NVARCHAR,Types.LONGNVARCHAR,Types.NCHAR,Types.LONGVARCHAR,Types.CLOB)
		,INT(Integer.class,Types.INTEGER,Types.SMALLINT,Types.BIGINT,Types.TINYINT)
		,FLOAT(Float.class,Types.FLOAT)
		,DOUBLE(Double.class,Types.DOUBLE)
		,DATE(Date.class,Types.DATE)
		,DATETIME(Timestamp.class,Types.TIMESTAMP,Types.TIME)
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
	
	public static String toClassName(String name) {
		StringBuffer ret=new StringBuffer("");
		for(String _colName:name.split("_")) {
			ret
			.append(_colName.substring(0, 1).toUpperCase())
			.append(_colName.substring(1).toLowerCase());
			
		}
		if(name.endsWith("_")) {
			ret.append("_");
		}
		return ret.toString();
	}
	
	
	public static String toPropName(String name) {
		String ret=toClassName(name);
		
		return ret.substring(0, 1).toLowerCase()+ret.substring(1, ret.length());
	}

	
	public static String refSrcPath(Class cls) {
		String ret=cls.getName();
		ret=ret.replace(".", "/");
		ret+=".java";
		return ret;
	}
	
}
