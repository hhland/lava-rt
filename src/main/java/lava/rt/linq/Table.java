package lava.rt.linq;

import java.lang.reflect.Field;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lava.rt.common.ReflectCommon;
import lava.rt.common.TextCommon;


public  class Table<M> extends View<M> {

	protected String pkName;
	
	protected Field pkField;
	
	protected Map<String,Field> fieldMap;
	
	protected Field[] insertFields =null,updateFields=null;
	
	public Table(DataContext dataContext,Class<M> classM,String tableName,String pkName)  {
		super(dataContext,classM,tableName);
		this.pkName=pkName;
		
		fieldMap=ReflectCommon.getDeclaredFields(classM);
		
		pkField=fieldMap.get(pkName);
		pkField.setAccessible(true);
		
		List<Field> linsertFields = new ArrayList<Field>();
		for (Field f : fieldMap.values()) {
            String fname = f.getName();
            if (ReflectCommon.isThis0(f) || fname.equalsIgnoreCase(pkName))  {
                continue;
            }
            f.setAccessible(true);
            linsertFields.add(f);
         }
		insertFields=linsertFields.toArray(new Field[linsertFields.size()]);
		
		
		List<Field> lupdateFields = new ArrayList<Field>();

	        for (Field f : fieldMap.values()) {
	            String fname = f.getName();
	            if (ReflectCommon.isThis0(f) || fname.equalsIgnoreCase(pkName))  {
	                continue;
	            }
	            f.setAccessible(true);
	            lupdateFields.add(f);
	        }
	        updateFields=lupdateFields.toArray(new Field[lupdateFields.size()]);      
		
	}
	
	

	protected static final String load_pattern="select * from {0} where {1}= {2} "; 
	public M load(int pk) throws SQLException{
		
		
		String sql=MessageFormat.format(load_pattern, this.tableName,this.pkName,
				pk
				);
		if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
		return dataContext.<M>executeQueryList(sql,classM).get(0);
	}
	
	public M load(String pk) throws SQLException{
		String pkVal="'"+pk+"'";
		String sql=MessageFormat.format(load_pattern, this.tableName,this.pkName,
				pkVal
				);
		if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
		return dataContext.<M>executeQueryList(sql,classM).get(0);
	}
	
	protected static final String loadLast_pattern="select * from {0} where {1}= (select max({1}) from {0} )"; 
	public M loadLast() throws SQLException{
		
		String sql=MessageFormat.format(loadLast_pattern, this.tableName,this.pkName
				
				);
		if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
		return dataContext.<M>executeQueryList(sql,classM).get(0);
	}
	
	protected static final String loadFirst_pattern="select * from {0} where {1}= (select min({1}) from {0} )"; 
	public M loadFirst() throws SQLException{
		
		String sql=MessageFormat.format(loadFirst_pattern, this.tableName,this.pkName
				
				);
		if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
		return dataContext.<M>executeQueryList(sql,classM).get(0);
	}
	
	
	public  int insert(M...models) throws SQLException {
		String sqlPattern = "insert into {0} ({1}) values ({2})", sql = "",
                sqlCacheKey = classM.getName() + ":insert", cols = pkName+",", vals = "?,";
       
		
        if (dataContext.SQL_CACHE.containsKey(sqlCacheKey)) {
            sql = dataContext.SQL_CACHE.get(sqlCacheKey);
        } else {

            for (Field field : insertFields) {

                String fname = field.getName();

                cols += " "+fname+",";
                vals += " ?,";

            }
            cols = TextCommon.trim(cols, ",");
            vals = TextCommon.trim(vals, ",");
            sql = MessageFormat.format(sqlPattern, tableName, cols, vals);
           
            dataContext.SQL_CACHE.put(sqlCacheKey, sql);
                
        }
        if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
        int re=0,insertsize = insertFields.length;
        
        
        Object[][] params = new Object[models.length][insertsize+1];
        try {
        for (int i = 0; i < models.length; i++) {
            M obj = models[i];
            params[i][0]=pkField.get(obj);
            for (int j = 0; j < insertsize; j++) {
                Field field = insertFields[j];
                params[i][j+1] = field.get(obj);
            }
        }
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        
        try {
           re+=	dataContext.executeInsert(sql, params);
        }catch(SQLException se){
        	SQLException nse=new SQLException(se.getMessage()+"\n"+sql);
        	throw nse;
        }
        
        
        return re;
        
	}
	
	
	public  float insertReturnPk(M...models) throws SQLException {
		String sqlPattern = "insert into {0} ({1}) values ({2})", sql = "",
                sqlCacheKey = classM.getName() + ":insertReturnPk", cols = "", vals = "";
       
		
        if (dataContext.SQL_CACHE.containsKey(sqlCacheKey)) {
            sql = dataContext.SQL_CACHE.get(sqlCacheKey);
        } else {

            for (Field field : insertFields) {

                String fname = field.getName();

                cols += " "+fname+",";
                vals += " ?,";

            }
            cols = TextCommon.trim(cols, ",");
            vals = TextCommon.trim(vals, ",");
            sql = MessageFormat.format(sqlPattern, tableName, cols, vals);
           
            dataContext.SQL_CACHE.put(sqlCacheKey, sql);
                
        }
        if(dataContext.DEBUG) {
    		dataContext.log(this.classM, sql);
    	}
        int re=0,insertsize = insertFields.length;
        
        
        Object[][] params = new Object[models.length][insertsize];
        try {
        for (int i = 0; i < models.length; i++) {
            M obj = models[i];
            for (int j = 0; j < insertsize; j++) {
                Field field = insertFields[j];
                params[i][j] = field.get(obj);
            }
        }
        } catch (Exception e) {
			e.printStackTrace();
		}
        
        int[] pks=null; 
        try {
           pks=	dataContext.executeInsertReturnPk(sql, params);
        }catch(SQLException se){
        	SQLException nse=new SQLException(se.getMessage()+"\n"+sql);
        	throw nse;
        }
        try {
        for(int i=0;i<pks.length;i++) {
            int pk=pks[i];
            M model=models[i];
            pkField.set(model, pk);
			
            re++;
        }
        } catch (Exception e) {
		}
        
        return re;
        
	}
	
	
	public  int update(M...models) throws SQLException{
		
			String sqlPattern = "update {0} set {1} where {2}=? ", key = "", sql = "",
	                sqlCacheKey = classM.getName() + ":update";

	        

	        if (dataContext.SQL_CACHE.containsKey(sqlCacheKey)) {
	            sql = dataContext.SQL_CACHE.get(sqlCacheKey);
	        } else {

	            
	            for (Field field : updateFields) {
	                String fname = field.getName();

	                key += MessageFormat.format(" {0} =? ,", fname);
	               
	            }
	            key = TextCommon.trim(key, ",");
	            //key=TextCommon.repeat(" `{0}` =? ", ",", updateFields.length);
	        	sql = MessageFormat.format(sqlPattern, this.tableName, key, this.pkName);
	            
	            dataContext.SQL_CACHE.put(sqlCacheKey, sql);
	           
	        }
	        if(dataContext.DEBUG) {
	    		dataContext.log(this.classM, sql);
	    	}
	        int updatesize = updateFields.length;
	        Object[][] params = new Object[models.length][updatesize + 1];
	        try {
	        for (int i = 0; i < models.length; i++) {
	            M obj = models[i];
	         
	            
	            	for (int j = 0; j < updatesize; j++) {
		                Field field = updateFields[j];
		                params[i][j] = field.get(obj);
		            }
					params[i][updatesize] = pkField.get(obj);
				


	        }
	        } catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return (int)dataContext.executeUpdate(sql, params);
		
	}
	
	public  int delete(M...models) throws SQLException{
		String sqlPattern = "delete from {0} where {1}=? ", sql = "",
                sqlCacheKey = classM.getName() + ":delete";

        if (dataContext.SQL_CACHE.containsKey(sqlCacheKey)) {
            sql = dataContext.SQL_CACHE.get(sqlCacheKey);
        } else {
            sql = MessageFormat.format(sqlPattern, tableName, pkName);
           
            dataContext.SQL_CACHE.put(sqlCacheKey, sql);
           
        }
        if(dataContext.DEBUG) {
        	
    		dataContext.log(this.classM, sql);
    	}
        int dlength = models.length;
        Object[][] params = new Object[dlength][1];
        try {
        for (int i = 0; i < dlength; i++) {
            M obj = models[i];
           params[i][0] = pkField.get(obj);
        }
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return (int)dataContext.executeUpdate(sql, params);
	}
	
	public Object getPk(M m) {
		Object re=null;
		try {
			re=pkField.get(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}
}
