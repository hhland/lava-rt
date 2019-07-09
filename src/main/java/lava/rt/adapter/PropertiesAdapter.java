package lava.rt.adapter;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import lava.rt.common.ReflectCommon;
import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;

public class PropertiesAdapter extends BaseAdapter<Properties>{

	protected static Log log=LogFactory.SYSTEM.getLog(PropertiesAdapter.class);
	
	
	
	public PropertiesAdapter(File... files) throws  IOException {
		super(new Properties());
		
		for(File file: files) {
			try(
					InputStream is=new FileInputStream(file);
				){
				_this.load(is);
				
			}
		}
	}

	public PropertiesAdapter(Properties properties) {
		super(properties);
	}
	
	
	public  float injection(String prefix,Object object) {
		return injection(prefix, object.getClass(),object);
	}
	
	public  float injection(String prefix,Class cls) {
		
		return injection(prefix,cls,null);
	}
	
	protected  float injection(String prefix,Class cls,Object object) {
		float re=0,total=_this.size();
		
		Map<String,Field> keyFields=ReflectCommon.allDeclaredFieldMap(cls);
		
		for(Iterator<Object> it=_this.keySet().iterator();it.hasNext();) {
			String _key=it.next().toString();
			
			if(!_key.startsWith(prefix))continue;
			
			String key=_key.substring(prefix.length());
			
			String value=_this.getProperty(key);
			
			if(keyFields.containsKey(key)) {
				Field field=keyFields.get(key);
				field.setAccessible(true);
				try {
					re+=set(field,object, value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error(e);
					continue;
				}
				
			}
			
		}
		
		float prec=re/total;
		return prec;
	}
	
	
	public  int injection(String prefix) {
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
	
	
    protected static int set(Field field,Object target,String value) throws IllegalArgumentException, IllegalAccessException {
    	
    	
    	if(String.class.equals(field.getType())) {
			 field.set(target, value); 
		 }
		 else if(int.class.equals(field.getType())||Integer.class.equals(field.getType())) {
			 field.setInt(target, Integer.parseInt(value)); 
		 }
		 else if(float.class.equals(field.getType())||Float.class.equals(field.getType())) {
			 field.setFloat(target, Float.parseFloat(value)); 
		 }
		 else if(double.class.equals(field.getType())||Double.class.equals(field.getType())) {
			 field.setDouble(target, Double.parseDouble(value)); 
		 }
		 else if(short.class.equals(field.getType())||Short.class.equals(field.getType())) {
			 field.setDouble(target, Short.parseShort(value)); 
		 }
		 else if(boolean.class.equals(field.getType())||Boolean.class.equals(field.getType())) {
			 
			 field.setBoolean(target, Boolean.parseBoolean(value)); 
		 }
		 else if(ReflectCommon.isArray(field)) {
		      String[] values=value.split(",");
		      field.set(target,values);
		 }else {
			 return 0;
		 }
    	
    	return 1;
    }
    
    
    
    public static String getSimpleFieldNames(String prefix,Class... clss) {
    	StringBuffer ret=new StringBuffer("");
    	for(Class cls :clss) {
    	
    		Map<String,Field> keyFields=ReflectCommon.allDeclaredFieldMap(cls);
    	    for(Entry<String, Field> ent: keyFields.entrySet()) {
    	    	ret
    	    	//.append(ent.getValue().getName())
    	    	.append(prefix)
    	    	.append(ent.getKey())
    	    	.append("=\n\n");
    	    }
    	}
    	return ret.toString();
    }
    
    public static String getClassFieldNames(String prefix,Class... clss) {
    	StringBuffer ret=new StringBuffer("");
    	for(Class cls :clss) {
    	
    		Map<String,Field> keyFields=ReflectCommon.allDeclaredFieldMap(cls);
    	    for(Entry<String, Field> ent: keyFields.entrySet()) {
    	    	ret
    	    	.append(prefix)
    	    	.append(cls.getName())
    	    	.append(".")
    	    	.append(ent.getKey())
    	    	.append("=\n\n");
    	    }
    	}
    	return ret.toString();
    }
	
}
