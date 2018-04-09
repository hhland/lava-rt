package lava.rt.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlCommon {

	private static Map<String,String> SQL_CACHE=new HashMap<String,String>();
	
	public static <T> int insert(Connection connection,T... objs) throws  SQLException {

        int success = 0;
        
        Class<T> cls = (Class<T>) objs[0].getClass();
        String sqlPattern = "insert into {0} ({1}) values ({2})", sql = "",
                sqlCacheKey = cls.getSimpleName() + ":insert", cols = "", vals = "";
        //Field[] fields = null;
        final SqlTable table = cls.getAnnotation(SqlTable.class);
        List<Field> insertFields = new ArrayList<Field>();

        for (Field f : cls.getDeclaredFields()) {
            String fname = f.getName();
            if (ReflectCommon.isThis0(f) || (LangCommon.isIn(fname, table.excludeColumns())
                    || (!table.insertPk() && fname.equalsIgnoreCase(table.pk())))) {
                continue;
            }
            insertFields.add(f);
        }

        if (SQL_CACHE.containsKey(sqlCacheKey)) {
            sql = SQL_CACHE.get(sqlCacheKey);
        } else {

            for (Field field : insertFields) {

                String fname = field.getName();

                cols += " `"+fname+"`,";
                vals += " ?,";

            }
            cols = TextCommon.trim(cols, ",");
            vals = TextCommon.trim(vals, ",");
            sql = MessageFormat.format(sqlPattern, table.name(), cols, vals);
           
             SQL_CACHE.put(sqlCacheKey, sql);
                
        }
        int insertsize = insertFields.size();
        Object[][] params = new Object[objs.length][insertsize];
        for (int i = 0; i < objs.length; i++) {
            T obj = objs[i];
            for (int j = 0; j < insertsize; j++) {
                Field field = insertFields.get(j);
                //String getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                //Method m=null;
				try {
					//m = obj.getClass().getDeclaredMethod(getterName);
					params[i][j] = field.get(obj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
                
            }
        }
        
        PreparedStatement preparedStatement= connection.prepareStatement(sql);
        
        for(Object[] param: params ) {
        	for(int i=1;i<=param.length;i++) {
        		preparedStatement.setObject(i, param[i]);	
        	}
        	success+=preparedStatement.executeUpdate();
        	preparedStatement.clearParameters();
        }
        preparedStatement.close();
        
        return success;
    }

	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SqlTable {

		String name();

		String pk();

		String[] excludeColumns() default {};

		boolean insertPk() default true;

	}
	
}
