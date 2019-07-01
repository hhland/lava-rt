package lava.rt.linq.src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
		try(PreparedStatement preparedStatement= connection.prepareStatement(sql);
				ResultSet resultSet=preparedStatement.executeQuery();
				){
		
	
		while(resultSet.next()) {
		   String table=resultSet.getString(1).toUpperCase();
		   String pkName=resultSet.getString(2).toUpperCase();
		   if(table.startsWith("BIN$"))continue;
		   tablePks.put(table, pkName);
		}
		}
		return tablePks;
	}

	@Override
	protected Map<String, List<ProcedureParamSrc>> loadProcedures(String databaseName) throws SQLException {
		// TODO Auto-generated method stub
		 Map<String, List<ProcedureParamSrc>> ret=new HashMap<>();
		 Map<String, StringBuffer> textMap=new HashMap<>();
		String sql="SELECT NAME,TEXT FROM ALL_SOURCE  WHERE TYPE='PROCEDURE' and OWNER='"+databaseName.toUpperCase()+"'";
		sql+=" order by NAME,LINE asc";
        try(PreparedStatement preparedStatement= connection.prepareStatement(sql);
        		ResultSet resultSet=preparedStatement.executeQuery();){
		while(resultSet.next()) {
		   String key=resultSet.getString(1)
				   ,line=resultSet.getString(2)
				   ;
		   StringBuffer sbr=textMap.get(key);
		   if(sbr==null) {
			   sbr=new StringBuffer("");
			   textMap.put(key, sbr);
			   
		   }
		   if(line.contains("--")) {
			   line=line.substring(0,line.indexOf("--"));
			   line.replace("\n", "");
		   }
		   sbr.append(line);
		}
        }
        
        for(Entry<String, StringBuffer> ent:textMap.entrySet()) {
        	
        	List<ProcedureParamSrc> paramSrcs=ret.get(ent.getKey());
        	
        	String procName=ent.getKey();
        	if(paramSrcs==null) {
        		paramSrcs=new ArrayList<>();
        		ret.put(procName, paramSrcs);
        	}
        	
        	String text=procName.toString(),t=text.toLowerCase(),p="";
        	int s=0,e=0;
        	s=t.indexOf(procName.toLowerCase())+procName.length();
        	t=t.substring(s);
        	
        	s=t.indexOf("(");
        	
        	if(s>=0&&s<5) {
        	     s++;
        	     
        	   
        	     
        	     
        	     
        	     p=text.substring(s,e);
        	}
        	
        	
        	
        	String[] params=p.split(",");
        	for(String param: params) {
        		//     (is_used OUT number, data_ratio OUT number, clob_rest OUT clob)

        		String[] subParams=param.trim().split(" ");
        		
        		String name=subParams[0].trim(),io=subParams[1].trim(),type=subParams[2];
        		ProcedureParamSrc src=new ProcedureParamSrc();
        		src.isOutput=io.toUpperCase().equals("OUT");
        		src.paramName=name;
        		src.sqlType=Types.VARCHAR;
        		src.cls=String.class;
        		paramSrcs.add(src);
        	}
        	
        }
		
		
		return ret;
	}

	
	@Override
	protected Class<? extends DataContextSrcGener> thisClass() {
		// TODO Auto-generated method stub
		return this.getClass();
	}
	
	
}
