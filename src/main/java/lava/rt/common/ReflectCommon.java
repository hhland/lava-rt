package lava.rt.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

public class ReflectCommon {

	

	

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface CopyField {

		String targetName();

		Class targetType() default String.class;

		String[] targetValueRef() default {};

	}
	
	public static float loadProperties(Properties properties,Object object) {
		return load(properties, object.getClass(),object);
	}
	
	public static float loadProperties(Properties properties,Class cls) {
		
		return load(properties,cls,null);
	}
	
	protected static float load(Properties properties,Class cls,Object object) {
		float re=0,total=properties.size();
		
		Map<String,Field> keyFields=new HashMap<String,Field>();
		
		for(Class clsi:getClass(cls).values()) {
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
	
	
	public static float loadMap(Map<String,String> map,Class cls,Object object) {
		float re=0,total=map.keySet().size();
		
		Map<String,Field> keyFields=new HashMap<String,Field>();
		
		for(Class clsi=cls ;clsi!=Object.class;clsi=cls.getSuperclass()) {
			Field[] fields=clsi.getDeclaredFields();
			for(Field field :fields) {
				String key=field.getName().toLowerCase();
				keyFields.put(key, field);
			}
		}
		
		for(Iterator<String> it=map.keySet().iterator();it.hasNext();) {
			String key=it.next().toString();
			String value=map.get(key);
			String fkey=key.replace('.', '_').toLowerCase();
			if(keyFields.containsKey(fkey)) {
				Field field=keyFields.get(fkey);
				field.setAccessible(true);
				try {
					field.set(object, value);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				re++;
			}
			
		}
		
		float prec=re/total;
		return prec;
	}
	
	

	

	private static <T> Map<String, T> toMap(T... values) {
		Map<String, T> map = new HashMap<>();

		for (int i = 0, j = 1; j < values.length; i += 2, j += 2) {
			map.put(values[i].toString(), values[j]);
		}

		return map;
	}

	protected static class CopyFieldStruct {

		public CopyFieldStruct(CopyField copyField, Field field) {
			this.dcCopyField = copyField;
			this.field = field;
		}

		public CopyField dcCopyField;
		public Field field;
		
		
		protected static Map<String, CopyFieldStruct> getCopyFieldStructs(Object source) {

			Map<String, CopyFieldStruct> sourceAnnFields = new HashMap<String, CopyFieldStruct>();

			for (Class<?> clazz = source.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
				Field[] sourceFields = clazz.getDeclaredFields();

				for (Field sourceField : sourceFields) {
					sourceField.setAccessible(true);
					CopyField ann = sourceField.getAnnotation(CopyField.class);
					if (ann != null) {
						CopyFieldStruct dfs = new CopyFieldStruct(ann, sourceField);
						sourceAnnFields.put(ann.targetName(), dfs);
					}
				}
			}

			return sourceAnnFields;
		}
	}

	
	
	public static boolean isThis0(Field field) {
        return field.getName().equals("this$0");
    }
	
	
	public static boolean isArray(Field field) {
        return field.getType().getName().startsWith("[L");
    }
	
	
	
	 //无限级内部类实例化
    public static <T> T newInstance(Class<T> cls) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        T t = null;
        String clsname = cls.getName();
        int i = clsname.lastIndexOf("$");
        if (i > -1) {
            Constructor constr = cls.getConstructors()[0];
            constr.setAccessible(true);
            String pname = clsname.substring(0, i);
            Class pcls = null;
            try {
                pcls = Class.forName(pname);
            } catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException(ex);
            }
            t = (T) constr.newInstance(newInstance(pcls));
        } else {
            t = (T) cls.newInstance();
        }
        return t;
    }
    
    
    public static Map<String,Field> getFields(Class cls){
    	Map<String,Field> fieldMap=new HashMap<String,Field>();
    	
    	for(Class cl :getClass(cls).values()) {
    		Field[] fields=cl.getFields();
    		Stream.of(fields).filter(f-> !fieldMap.containsKey(f.getName()))
    		.forEach(f->fieldMap.put(f.getName(), f) );    		
    	}
    	return fieldMap;
    }
    
    public static Map<String,Field> getDeclaredFields(Class cls){
    	Map<String,Field> fieldMap=new HashMap<String,Field>();
    	
    	for(Class cl:getClass(cls).values()) {
    		Field[] fields=cl.getDeclaredFields();
    		Stream.of(fields).filter(f-> !fieldMap.containsKey(f.getName()))
    		.forEach(f->fieldMap.put(f.getName(), f) );    		
    	}
    	return fieldMap;
    }
    
    
    public static Map<String,Class> getClass(Class cls){
    	Map<String,Class> re=new HashMap<String,Class>();
    	
    	for(Class cl=cls;!Object.class.equals(cl);cl=cl.getSuperclass()) {
    		re.put(cl.getName(), cl);	
    	}
    	return re;
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
		 else if(isArray(field)) {
		      String[] values=value.split(",");
		      field.set(target,values);
		 }else {
			 return 0;
		 }
    	}catch(Exception e) {
    		
    	}
    	return 1;
    }
    
    
    public static void close(Object...objs) {
    	for(Object obj :objs) {
    		try {
				obj.getClass().getMethod("close").invoke(obj);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

}
