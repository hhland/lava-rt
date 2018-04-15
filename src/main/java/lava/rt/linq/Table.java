package lava.rt.linq;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import lava.rt.common.LangCommon;
import lava.rt.common.ReflectCommon;
import lava.rt.common.SqlCommon;
import lava.rt.common.TextCommon;
import lava.rt.common.SqlCommon.SqlTable;

public  class Table<M> extends View<M> {

	protected String pkName;
	
	public Table(DataContext dataContext,Class<M> classM,String tableName,String pkName) {
		super(dataContext,classM,tableName);
		this.pkName=pkName;
		
	}
	
	public M load(Object pk) throws SQLException{
		String pattern="select * from {0} where {1}= {2}";
		String pkVal=null;
		if(pk instanceof String) {
			pkVal="'"+pk+"'";
		}else {
			pkVal=pk.toString() ;
		}
		String sql=MessageFormat.format(pattern, this.tableName,this.pkName,
				pkVal
				);
		return dataContext.<M>executeQueryList(sql,classM).get(0);
	}
	
	public  int insert(M...models) throws SQLException {
		String sqlPattern = "insert into {0} ({1}) values ({2})", sql = "",
                sqlCacheKey = classM.getName() + ":insert", cols = "", vals = "";
       
		Field pkField=null;
		try {
			pkField = classM.getDeclaredField(pkName);
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			throw new SQLException("pkField not found:"+pkName);
		}
		
        List<Field> insertFields = new ArrayList<Field>();
        
        
        for (Field f : ReflectCommon.getFields(classM).values()) {
            String fname = f.getName();
            if (ReflectCommon.isThis0(f) || fname.equalsIgnoreCase(pkName))  {
                continue;
            }
            f.setAccessible(true);
            insertFields.add(f);
        }
        

        if (dataContext.SQL_CACHE.containsKey(sqlCacheKey)) {
            sql = dataContext.SQL_CACHE.get(sqlCacheKey);
        } else {

            for (Field field : insertFields) {

                String fname = field.getName();

                cols += " `"+fname+"`,";
                vals += " ?,";

            }
            cols = TextCommon.trim(cols, ",");
            vals = TextCommon.trim(vals, ",");
            sql = MessageFormat.format(sqlPattern, tableName, cols, vals);
           
            dataContext.SQL_CACHE.put(sqlCacheKey, sql);
                
        }
        int re=0,insertsize = insertFields.size();
        
        
        Object[][] params = new Object[models.length][insertsize];
        for (int i = 0; i < models.length; i++) {
            M obj = models[i];
            for (int j = 0; j < insertsize; j++) {
                Field field = insertFields.get(j);
               
				try {
					
					params[i][j] = field.get(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
                
            }
        }
        
        int[] pks= dataContext.executeInsert(sql, params);
        for(int i=0;i<pks.length;i++) {
            int pk=pks[i];
            M model=models[i];
            try {
				pkField.set(model, pk);
			} catch (Exception e) {
			}
            re++;
        }
        
        return re;
        
	}
	
	
	public  int update(M...models) throws SQLException{
		
			String sqlPattern = "update {0} set {1} where {2}=? ", key = "", sql = "",
	                sqlCacheKey = classM.getName() + ":update";

	        Field pkField=null;
			try {
				pkField = classM.getDeclaredField(pkName);
			} catch (NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				throw new SQLException("pkField not found:"+pkName);
			}
	        List<Field> updateFields = new ArrayList<Field>();

	        for (Field f : classM.getDeclaredFields()) {
	            String fname = f.getName();
	            if (ReflectCommon.isThis0(f) || fname.equalsIgnoreCase(pkName))  {
	                continue;
	            }
	            f.setAccessible(true);
	            updateFields.add(f);
	        }

	        if (dataContext.SQL_CACHE.containsKey(sqlCacheKey)) {
	            sql = dataContext.SQL_CACHE.get(sqlCacheKey);
	        } else {

	            
	            for (Field field : updateFields) {
	                String fname = field.getName();

	                key += MessageFormat.format(" `{0}` =? ,", fname);
	               
	            }
	            key = TextCommon.trim(key, ",");
	            sql = MessageFormat.format(sqlPattern, this.tableName, key, this.pkName);
	            
	            dataContext.SQL_CACHE.put(sqlCacheKey, sql);
	           
	        }
	        int updatesize = updateFields.size();
	        Object[][] params = new Object[models.length][updatesize + 1];
	        for (int i = 0; i < models.length; i++) {
	            M obj = models[i];
	         
	            try {
	            	for (int j = 0; j < updatesize; j++) {
		                Field field = updateFields.get(j);
		                params[i][j] = field.get(obj);
		            }
					params[i][updatesize] = pkField.get(obj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


	        }
			
			return dataContext.executeUpdate(sql, params);
		
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
        int dlength = models.length;
        Object[][] params = new Object[dlength][1];
        for (int i = 0; i < dlength; i++) {
            M obj = models[i];
            Field pkField;
			try {
				pkField = classM.getField(pkName);
				params[i][1] = pkField.get(obj);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
        return dataContext.executeUpdate(sql, params);
	}
}
