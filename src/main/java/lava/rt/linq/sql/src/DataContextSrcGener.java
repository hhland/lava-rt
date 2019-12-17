package lava.rt.linq.sql.src;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

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
import java.util.*;

import javax.sql.DataSource;

import lava.rt.execption.CommandExecuteExecption;
import lava.rt.linq.DataContext;
import lava.rt.linq.Entity;
import lava.rt.linq.sql.*;
import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;



public abstract class DataContextSrcGener   {

	public  long serialVersionUID=1L;
	
	protected abstract Class<? extends DataContextSrcGener> thisClass();
	
	protected Log log=LogFactory.SYSTEM.getLog(thisClass());
	
	protected Connection connection;
	
	protected Set<String> columnNames=new HashSet<>();
	
	public SrcEvent srcEvent=new SrcEvent();
	
	public DataContextSrcGener(Connection connection) {
		this.connection=connection;
	}
	
	




	
	
    public  void saveIntfSrcTo(File srcIntf,Class clsIntf,String databaseName) throws SQLException, IOException {
		
    	String src=toIntfSrc(clsIntf, databaseName);
    	srcIntf.delete();
    	srcIntf.createNewFile();
    	srcIntf.setWritable(true);
		try(FileWriter fw=new FileWriter(srcIntf)){
			fw.write(src);
		}catch(IOException ioe) {
			log.equals(srcIntf);
			ioe.printStackTrace();
			throw ioe;
		}
		
		
	}
    
     public  void saveImplSrcTo(Class clsIntf,File srcImpl,Class clsImpl,String databaseName) throws SQLException, IOException {
		
    	 String src=toImplSrc(clsImpl,clsIntf, databaseName);
		srcImpl.delete();
		srcImpl.createNewFile();
		srcImpl.setWritable(true);
		try(FileWriter fw=new FileWriter(srcImpl)){
			fw.write(src);
		}catch(IOException ioe) {
			log.equals(srcImpl);
			ioe.printStackTrace();
			throw ioe;
		}
	}
	
     
   
	
	
	


	public String toIntfSrc(Class cls,String databaseName) throws SQLException {
		StringBuffer src=new StringBuffer(getFileInfo(databaseName));
		if(cls.getPackage()!=null) {
		 src.append("package "+cls.getPackage().getName()+"; \n\n");
		}
		
		src
		.append("import "+DataContext.class.getPackage().getName()+".*; \n")
		.append("import "+DataSourceContext.class.getPackage().getName()+".*; \n")
		
		.append("import "+ List.class.getPackage().getName()+".*; \n")
		
		.append("import "+ SQLException.class.getPackage().getName()+".*; \n")
		.append("import "+BigDecimal.class.getPackage().getName()+".*; \n\n\n")
		.append("import "+Serializable.class.getPackage().getName()+".*; \n\n\n")
		.append("import "+Date.class.getName()+"; \n\n\n")
		;
		
		
		
		src.append("public interface "+cls.getSimpleName()+" extends "+SqlDataContext.class.getName()+"{ \n\n");
		
		src
		.append("\tpublic static final long serialVersionUID="+serialVersionUID+";\n\n")
		//.append("\t@Override\r\n" + 
		//		"\tprotected Class thisClass() {return this.getClass(); }\n\n")
		//.append("\t public "+cls.getSimpleName()+"("+DataSource.class.getSimpleName()+"... dataSources)throws "+Exception.class.getSimpleName()+"{ super(dataSources);  } \n\n")
		;
		
		Set<String> tables=new HashSet<>(),views=loadViews(databaseName);
		
		Map<String, List<ProcedureParamSrc>>procs=loadProcedures(databaseName);
		
		Map<String, String> tablesPks=loadTablesPks(databaseName);
		
		srcEvent.onViewsLoaded(src,views);
		srcEvent.onTablesLoaded(src,tablesPks);
		srcEvent.onProceduresLoaded(src,procs);
		
			tables=tablesPks.keySet();
		
		
		
		
		
		
		src.append("\n\n");
		
		//tables.addAll(views);
		for(String table:tables) {
			String pkName=null;
			String tn=table;
			if(tablesPks.containsKey(tn)) {
				pkName=tablesPks.get(tn);
			}
			TableSrc tableSrc=new TableSrc(tn,pkName);
			srcEvent.onTableSrcAppend(src,tableSrc);
			src.append(tableSrc.toSrc(cls));
		}
		
		for(String table:views) {
			
			String tn=table;
			TableSrc tableSrc=new TableSrc(tn,null);
			srcEvent.onViewSrcAppend(src,tableSrc);
			src.append(tableSrc.toSrc(cls));
		}
		
        for(java.util.Map.Entry<String, List<ProcedureParamSrc>> ent : procs.entrySet() ) {
			
			String tn=ent.getKey();
			ProcedureSrc tableSrc=new ProcedureSrc(tn,ent.getValue());
			srcEvent.onProcedureIntfSrcAppend(src,tableSrc);
			src.append(tableSrc.toIntfSrc()).append("\n\n");
		}
		
		src
		.append("\tpublic final static Criteria CRITERIA=new Criteria();\n\n")
		.append("\tpublic  static class Criteria{ \n\n")
		.append("\t\tprivate Criteria() {} \n\n")
		.append("\t\tpublic static final Column \n");
		
		for(String colName:columnNames) {
			ColumnSrc columnSrc=new ColumnSrc(colName, toPropName(colName));
			srcEvent.onColumnSrcAppend(src, columnSrc);
			src.append("\t\t"+ columnSrc.className+" = new Column(\""+columnSrc.columnName+"\",\""+columnSrc.className+"\"),\n" );
		}
		src.deleteCharAt(src.length()-2);
		src.append( "\t\t;\n\n")
		.append("\t} \n\n")
		;
		src.append("\n\n\n} //end");
		
		return src.toString();
	}
	
	
	
	public String toImplSrc(Class cls,Class intfCls,String databaseName) throws SQLException {
		StringBuffer src=new StringBuffer(getFileInfo(databaseName));
		if(cls.getPackage()!=null) {
		 src.append("package "+cls.getPackage().getName()+"; \n\n");
		}
		
		
		
		
		src.append("import "+DataContext.class.getPackage().getName()+".*; \n")
		
		.append("import "+DataSourceContext.class.getPackage().getName()+".*; \n")
		.append("import "+ List.class.getPackage().getName()+".*; \n")
		
		.append("import "+ SQLException.class.getPackage().getName()+".*; \n")
		.append("import "+DataSource.class.getPackage().getName()+".*; \n\n\n")
		.append("import "+Date.class.getName()+"; \n\n\n")
		;
		
		
		
		
		
		
		src.append("public abstract class "+cls.getSimpleName()+" extends "+DataSourceContext.class.getName()+" implements "+intfCls.getName()+"{ \n\n");
		
		
	
		
		//src
		//.append("\tprivate static final long serialVersionUID=1L;\n\n")
		//.append("\t@Override\r\n" + 
		//		"\tprotected Class thisClass() {return this.getClass(); }\n\n")
		//.append("\t public "+cls.getSimpleName()+"("+DataSource.class.getSimpleName()+"... dataSources)throws "+Exception.class.getSimpleName()+"{ super(dataSources);  } \n\n")
		//;
		
		Set<String> tables=new HashSet<>(),views=loadViews(databaseName);
		
		Map<String, List<ProcedureParamSrc>>procs=loadProcedures(databaseName);
		
		Map<String, String> tablesPks=loadTablesPks(databaseName);
		
		
	    tables=tablesPks.keySet();
		
	    srcEvent.onViewsLoaded(src,views);
		srcEvent.onTablesLoaded(src,tablesPks);
		srcEvent.onProceduresLoaded(src,procs);
		
		
		for(String table:tables) {
			
			String pkName="ID";
			
			String tn=table.toUpperCase()
					
					;
			if(views.contains(tn))continue;
			
			if(tablesPks.containsKey(tn)) {
				pkName=tablesPks.get(tn);
			}
			TableSrc tableSrc=new TableSrc(tn, pkName);
			srcEvent.onTableSrcAppend(src, tableSrc);
			src.append("\t public final Table<"+tableSrc.className+"> "+tableSrc.tableName+"=tableCreate("+tableSrc.className+".class,\""+tableSrc.tableName+"\",\""+tableSrc.pkName+"\");\n");
		}
		
		src.append("\n\n");
		
		for(String table:views) {
			
			
			String tn=table.toUpperCase()
					
					;
			TableSrc tableSrc=new TableSrc(tn,null);
			srcEvent.onViewSrcAppend(src, tableSrc);
			src.append("\t public final View<"+tableSrc.className+"> "+tableSrc.tableName+"=viewCreate("+tableSrc.className+".class,\""+tableSrc.tableName+"\");\n");
		}
		
		src.append("\n\n");
		
		
		
        for(java.util.Map.Entry<String, List<ProcedureParamSrc>> ent : procs.entrySet() ) {
			
			String tn=ent.getKey();
			ProcedureSrc tableSrc=new ProcedureSrc(tn,ent.getValue());
			srcEvent.onProcedureImplSrcAppend(src,tableSrc);
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
				.append(") throws "+CommandExecuteExecption.class.getSimpleName()+"; ")
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
			.append(") throws "+CommandExecuteExecption.class.getSimpleName()+" {")
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
	
	public class ColumnSrc{
		
		public String columnName,className;

		public ColumnSrc(String columnName, String className) {
			super();
			this.columnName = columnName;
			this.className = className;
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
			if(_colName.length()==0)continue;
			try {
				int i=Integer.parseInt(_colName);
				ret.append("_").append(i);
			}catch(Exception ex) {
				ret
				.append(_colName.substring(0, 1).toUpperCase())
				.append(_colName.substring(1).toLowerCase());
				}
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
