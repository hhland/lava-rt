package lava.rt.common;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class ReflectCommon {

	
	
	
	
	
	

	

	

	

	
	
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
   
    
    
    public static Map<String,Method> getMethods(Class cls){
    	Map<String,Method> fieldMap=new HashMap<>();
    	
    	for(Class cl :getClasses(cls).values()) {
    		Method[] fields=cl.getMethods();
    		Stream.of(fields).filter(f-> !fieldMap.containsKey(f.getName()))
    		.forEach(f->fieldMap.put(f.getName(), f) );    		
    	}
    	return fieldMap;
    }
    
    public static Map<String,Method> getDeclaredMethods(Class cls){
    	Map<String,Method> fieldMap=new HashMap<>();
    	
    	for(Class cl:getClasses(cls).values()) {
    		Method[] fields=cl.getDeclaredMethods();
    		Stream.of(fields).filter(f-> !fieldMap.containsKey(f.getName()))
    		.forEach(f->fieldMap.put(f.getName(), f) );    		
    	}
    	return fieldMap;
    }
    
    
    public static Map<String,Field> getFields(Class cls){
    	Map<String,Field> fieldMap=new HashMap<String,Field>();
    	
    	for(Class cl :getClasses(cls).values()) {
    		Field[] fields=cl.getFields();
    		Stream.of(fields).filter(f-> !fieldMap.containsKey(f.getName()))
    		.forEach(f->fieldMap.put(f.getName(), f) );    		
    	}
    	return fieldMap;
    }
    
    public static Map<String,Field> getDeclaredFields(Class cls){
    	Map<String,Field> fieldMap=new HashMap<String,Field>();
    	
    	for(Class cl:getClasses(cls).values()) {
    		Field[] fields=cl.getDeclaredFields();
    		Stream.of(fields).filter(f-> !fieldMap.containsKey(f.getName()))
    		.forEach(f->fieldMap.put(f.getName(), f) );    		
    	}
    	return fieldMap;
    }
    
    
    public static Map<String,Class> getClasses(Class cls){
    	Map<String,Class> re=new HashMap<String,Class>();
    	
    	for(Class cl=cls;!Object.class.equals(cl);cl=cl.getSuperclass()) {
    		re.put(cl.getName(), cl);	
    	}
    	return re;
    }
    
    public static Map<String,Class> getClasses(Package pack){
    	return null;
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
