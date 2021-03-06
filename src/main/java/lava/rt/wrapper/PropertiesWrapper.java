package lava.rt.wrapper;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.Map.Entry;


import lava.rt.common.ReflectCommon;


public class PropertiesWrapper extends BaseWrapper<Properties>{

	protected static LoggerWrapper log=LoggerWrapper.CONSOLE;
	
	
	
	public PropertiesWrapper(File... files) throws  IOException {
		super(new Properties());
		
		for(File file: files) {
			try(
					InputStream is=new FileInputStream(file);
				){
				self.load(is);
				
			}
		}
	}

	public PropertiesWrapper(Properties properties) {
		super(properties);
	}
	
	
	public  float injection(String prefix,Object object) {
		return injection(prefix, object.getClass(),object);
	}
	
	public  float injection(String prefix,Class cls) {
		
		return injection(prefix,cls,null);
	}
	
	protected  float injection(String prefix,Class cls,Object object) {
		float re=0,total=self.size();
		
		Map<String,Field> keyFields=ReflectCommon.getAllDeclaredFieldMap(cls);
		
		for(Iterator<Object> it=self.keySet().iterator();it.hasNext();) {
			String _key=it.next().toString();
			
			if(!_key.startsWith(prefix))continue;
			
			String key=_key.substring(prefix.length());
			
			String value=self.getProperty(key);
			
			if(keyFields.containsKey(key)) {
				Field field=keyFields.get(key);
				field.setAccessible(true);
				try {
					re+=set(field,object, value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.info(e.getMessage());
					continue;
				}
				
			}
			
		}
		
		float prec=re/total;
		return prec;
	}
	
	
	public  int injection(String prefix) {
        int re=0,total=self.size();
        
        for(Iterator<Object> it=self.keySet().iterator();it.hasNext();) {
        	String key=it.next().toString();
        	String value= self.get(key).toString();
        	
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
			 field.set(target, Integer.parseInt(value)); 
		 }
		 else if(float.class.equals(field.getType())||Float.class.equals(field.getType())) {
			 field.set(target, Float.parseFloat(value)); 
		 }
		 else if(double.class.equals(field.getType())||Double.class.equals(field.getType())) {
			 field.set(target, Double.parseDouble(value)); 
		 }
		 else if(short.class.equals(field.getType())||Short.class.equals(field.getType())) {
			 field.set(target, Short.parseShort(value)); 
		 }
		 else if(boolean.class.equals(field.getType())||Boolean.class.equals(field.getType())) {
			 
			 field.set(target, Boolean.parseBoolean(value)); 
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
    	
    		Map<String,Field> keyFields=ReflectCommon.getAllDeclaredFieldMap(cls);
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
    	
    		Map<String,Field> keyFields=ReflectCommon.getAllDeclaredFieldMap(cls);
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
