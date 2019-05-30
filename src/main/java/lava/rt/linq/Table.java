package lava.rt.linq;

import java.lang.reflect.Field;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lava.rt.common.ReflectCommon;
import lava.rt.common.TextCommon;


public  class Table<M extends Entry> extends View<M> {

	protected final String pkName;
	
	protected final Field pkField;
	
	
	
	protected final Field[] insertFields,updateFields;
	
	public Table(DataContext dataContext,Class<M> entryClass,String tableName,String pkName)  {
		super(dataContext,entryClass,tableName);
		this.pkName=pkName;
		
		
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
	
	

	
	
	public M load(Object pk) throws SQLException{
	
		String sql=MessageFormat.format("select * from {0} where {1}= ? ", this.tableName,this.pkName
				);
		if(dataContext.DEBUG) {
    		dataContext.LOGGER.log(this.entryClass, sql);
    	}
		List<M> entrys=dataContext.<M>executeQueryList(sql,entryClass,pk);
		M ret=null;
		if(entrys.size()==1) {
			ret=entrys.get(0);
		}else  {
			throw new SQLException("there has not only 1 but "+entrys.size()+" entrys in ["+tableName+"] has ["+pk+"]");
		}
		return ret;
	}
	
	
	
	
	public <E extends M> int insert(E[] entrys) throws SQLException {
		if(entrys.length==0)return 0;
		String sqlPattern = "insert into {0} ({1}) values ({2})", sql = "",
                sqlCacheKey = entryClass.getName() + ":insert", cols = pkName+",", vals = "?,";
       
		
        if (dataContext.sqlMap.containsKey(sqlCacheKey)) {
            sql = dataContext.sqlMap.get(sqlCacheKey);
        } else {

            for (Field field : insertFields) {

                String fname = field.getName();

                cols += " "+fname+",";
                vals += " ?,";

            }
            cols = TextCommon.trim(cols, ",");
            vals = TextCommon.trim(vals, ",");
            sql = MessageFormat.format(sqlPattern, tableName, cols, vals);
           
            dataContext.sqlMap.put(sqlCacheKey, sql);
                
        }
        if(dataContext.DEBUG) {
    		dataContext.LOGGER.log(this.entryClass, sql);
    	}
        int re=0,insertsize = insertFields.length;
        
        
        Object[][] params = new Object[entrys.length][insertsize+1];
        try {
        for (int i = 0; i < entrys.length; i++) {
            M obj = entrys[i];
            params[i][0]=pkField.get(obj);
            for (int j = 0; j < insertsize; j++) {
                Field field = insertFields[j];
                params[i][j+1] = field.get(obj);
            }
        }
        } catch (Exception e) {
			  throw new SQLException(e);
		}
        
        
       
        if(params.length>1) {
				re=dataContext.executeBatch(sql, params);
		}else {
				re=dataContext.executeUpdate(sql, params[0]);
		}
        
        
        
        return re;
        
	}
	
	
	public <E extends M> int insert(E entry) throws SQLException {
		
		String sqlPattern = "insert into {0} ({1}) values ({2})", sql = "",
                sqlCacheKey = entryClass.getName() + ":insertReturnPk", cols = "", vals = "";
       
		
        if (dataContext.sqlMap.containsKey(sqlCacheKey)) {
            sql = dataContext.sqlMap.get(sqlCacheKey);
        } else {

            for (Field field : insertFields) {

                String fname = field.getName();

                cols += " "+fname+",";
                vals += " ?,";

            }
            cols = TextCommon.trim(cols, ",");
            vals = TextCommon.trim(vals, ",");
            sql = MessageFormat.format(sqlPattern, tableName, cols, vals);
           
            dataContext.sqlMap.put(sqlCacheKey, sql);
                
        }
        if(dataContext.DEBUG) {
    		dataContext.LOGGER.log(this.entryClass, sql);
    	}
        int re=0,insertsize = insertFields.length;
        
        Object[] param=new Object[insertsize];
       
        
        int pk=0; 
        try {
          for (int j = 0; j < insertsize; j++) {
                Field field = insertFields[j];
                param[j] = field.get(entry);
           }
           pk=	dataContext.executeInsertReturnPk(sql, param);
           pkField.set(entry, pk);
        }catch(SQLException se){
        	SQLException nse=new SQLException(se.getMessage()+"\n"+sql);
        	throw nse;
        }catch (Exception e) {
        	throw new SQLException(e);
        }
        
        
        return re;
        
	}
	
	
	public <E extends M> int update(E...entrys) throws SQLException{
		   if(entrys.length==0)return 0;
			String sqlPattern = "update {0} set {1} where {2}=? ", key = "", sql = "",
	                sqlCacheKey = entryClass.getName() + ":update";

	        

	        if (dataContext.sqlMap.containsKey(sqlCacheKey)) {
	            sql = dataContext.sqlMap.get(sqlCacheKey);
	        } else {

	            
	            for (Field field : updateFields) {
	                String fname = field.getName();

	                key += MessageFormat.format(" {0} =? ,", fname);
	               
	            }
	            key = TextCommon.trim(key, ",");
	            //key=TextCommon.repeat(" `{0}` =? ", ",", updateFields.length);
	        	sql = MessageFormat.format(sqlPattern, this.tableName, key, this.pkName);
	            
	            dataContext.sqlMap.put(sqlCacheKey, sql);
	           
	        }
	        if(dataContext.DEBUG) {
	    		dataContext.LOGGER.log(this.entryClass, sql);
	    	}
	        int updatesize = updateFields.length;
	        Object[][] params = new Object[entrys.length][updatesize + 1];
	        try {
	        for (int i = 0; i < entrys.length; i++) {
	            M obj = entrys[i];
	         
	            
	            	for (int j = 0; j < updatesize; j++) {
		                Field field = updateFields[j];
		                params[i][j] = field.get(obj);
		            }
					params[i][updatesize] = getPk(obj);
				


	        }
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				  throw new SQLException(e);
			}
	        int re=0;
			if(params.length>1) {
				re=dataContext.executeBatch(sql, params);
			}else {
				re=dataContext.executeUpdate(sql, params[0]);
			}
			return re;
		
	}
	
	public <E extends M> int delete(E...entrys) throws SQLException{
		if(entrys.length==0)return 0;
		String sqlPattern = "delete from {0} where {1}=? ", sql = "",
                sqlCacheKey = entryClass.getName() + ":delete";

        if (dataContext.sqlMap.containsKey(sqlCacheKey)) {
            sql = dataContext.sqlMap.get(sqlCacheKey);
        } else {
            sql = MessageFormat.format(sqlPattern, tableName, pkName);
           
            dataContext.sqlMap.put(sqlCacheKey, sql);
           
        }
        if(dataContext.DEBUG) {
        	
    		dataContext.LOGGER.log(this.entryClass, sql);
    	}
        int dlength = entrys.length;
        Object[][] params = new Object[dlength][1];
        
        for (int i = 0; i < dlength; i++) {
            M obj = entrys[i];
           params[i][0] = getPk(obj);
        }
        
        
        int re=0;
		if(params.length>1) {
			re=dataContext.executeBatch(sql, params);
		}else {
			re=dataContext.executeUpdate(sql, params[0]);
		}
        return re;
	}
	
	
	public int truncate() throws SQLException {
		String sql="truncate table "+tableName;
		int re=dataContext.executeUpdate(sql);
		return re;
	}
	
	
	public <E extends M> Object getPk(E entry) {
		Object ret=null;
		try {
			ret= pkField.get(entry);
		} catch (IllegalArgumentException | IllegalAccessException e) {}
		return ret;
	}
	
}
