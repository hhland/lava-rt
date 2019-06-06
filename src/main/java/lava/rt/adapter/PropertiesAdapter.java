package lava.rt.adapter;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import lava.rt.common.ReflectCommon;

public class PropertiesAdapter extends BaseAdapter<Properties>{

	
	

	public PropertiesAdapter(Properties properties) {
		super(properties);
	}
	
	
	public  float load(Object object) {
		return load( object.getClass(),object);
	}
	
	public  float load(Class cls) {
		
		return load(cls,null);
	}
	
	protected  float load(Class cls,Object object) {
		float re=0,total=_this.size();
		
		Map<String,Field> keyFields=ReflectCommon.getDeclaredFields(cls);
		
		for(Iterator<Object> it=_this.keySet().iterator();it.hasNext();) {
			String key=it.next().toString();
			String value=_this.getProperty(key);
			
			if(keyFields.containsKey(key)) {
				Field field=keyFields.get(key);
				field.setAccessible(true);
				try {
					re+=set(field,object, value);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					  
					continue;
				}
				
			}
			
		}
		
		float prec=re/total;
		return prec;
	}
	
	
	public  int injection() {
        int re=0,total=_this.size();
        
        for(Iterator<Object> it=_this.keySet().iterator();it.hasNext();) {
        	String key=it.next().toString();
        	String value= _this.get(key).toString();
        	
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
        
        return re;
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
		 else if(double.class.equals(field.getType())||Double.class.equals(field.getType())) {
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
    
    
    public static String toTemplate(Class... clss) {
    	StringBuffer ret=new StringBuffer("");
    	for(Class cls :clss) {
    	
    		Map<String,Field> keyFields=ReflectCommon.getDeclaredFields(cls);
    	    for(Entry<String, Field> ent: keyFields.entrySet()) {
    	    	ret
    	    	.append(cls.getName())
    	    	.append(".")
    	    	.append(ent.getKey())
    	    	.append("=\n\n");
    	    }
    	}
    	return ret.toString();
    }
	
}
