package lava.rt.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PropertiesCommon {

	
	
	public static float load(Properties properties,Object object) {
		return load(properties, object.getClass(),object);
	}
	
	public static float load(Properties properties,Class cls) {
		
		return load(properties,cls,null);
	}
	
	protected static float load(Properties properties,Class cls,Object object) {
		float re=0,total=properties.size();
		
		Map<String,Field> keyFields=new HashMap<String,Field>();
		
		for(Class clsi:ReflectCommon.getClasses(cls).values()) {
			Field[] fields=clsi.getDeclaredFields();
			for(Field field :fields) {
				String key=field.getName().toLowerCase();
				keyFields.put(key, field);
			}
		}
		
		for(Iterator<Object> it=properties.keySet().iterator();it.hasNext();) {
			String key=it.next().toString();
			String value=properties.getProperty(key);
			String fkey=key.replace('.', '_').toLowerCase();
			if(keyFields.containsKey(fkey)) {
				Field field=keyFields.get(fkey);
				field.setAccessible(true);
				try {
					re+=set(field,object, value);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				
			}
			
		}
		
		float prec=re/total;
		return prec;
	}
	
	
	public static float injection(Properties properties) {
        float re=0,total=properties.size();
        
        for(Iterator<Object> it=properties.keySet().iterator();it.hasNext();) {
        	String key=it.next().toString();
        	String value= properties.get(key).toString();
        	
        	String fieldName=key.substring(key.lastIndexOf(".")+1)
        			,className=key.substring(0,key.length()-fieldName.length()-1)
        			;
        	Class cls=null;
        	Field field=null;
        	try {
				 cls=Class.forName(className);
				 field=cls.getDeclaredField(fieldName);
				 field.setAccessible(true);
				 
				 re+=set(field,null,value);
			} finally {continue;}
        }
        
        
        float prec=re/total;
        return prec;
    }
	
	
protected static int set(Field field,Object target,String value) {
    	
    	try {
    	if(String.class.equals(field.getType())) {
			 field.set(null, value); 
		 }
		 else if(int.class.equals(field.getType())||Integer.class.equals(field.getType())) {
			 field.setInt(null, Integer.parseInt(value)); 
		 }
		 else if(float.class.equals(field.getType())||Float.class.equals(field.getType())) {
			 field.setFloat(null, Float.parseFloat(value)); 
		 }
		 else if(double.class.equals(field.getType())||double.class.equals(field.getType())) {
			 field.setDouble(null, Double.parseDouble(value)); 
		 }
		 else if(short.class.equals(field.getType())||Short.class.equals(field.getType())) {
			 field.setDouble(null, Short.parseShort(value)); 
		 }
		 else if(boolean.class.equals(field.getType())||Boolean.class.equals(field.getType())) {
			 
			 field.setBoolean(null, Boolean.parseBoolean(value)); 
		 }
		 else if(ReflectCommon.isArray(field)) {
		      String[] values=value.split(",");
		      field.set(target,values);
		 }else {
			 return 0;
		 }
    	}catch(Exception e) {
    		
    	}
    	return 1;
    }
    
}
