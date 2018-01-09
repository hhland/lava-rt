package lava.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum Beans {

	;
	
	public static int copy(Object source,Object target){
		
		   int re=0;
		   for(Method sourceMethod:source.getClass().getMethods()){
			   
			   if(!sourceMethod.getName().startsWith("get"))continue;
			   String getterName=sourceMethod.getName(),
					   fieldName=getterName.substring("get".length(), getterName.length()),
					   settterName="set"+fieldName;
			   if("Class".equals(fieldName))continue; 
			   try{
			
				   Object sourceVal=sourceMethod.invoke(source);
				   target.getClass()
				   .getMethod(settterName, sourceMethod.getReturnType())
				   .invoke(target, sourceVal);
				   re++;
			   }catch(Exception e){}
			   
		   }
		   return re;
          		
	}
	
	
	
	public static void attr(Object obj,String field,Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		String setterName="set"+lava.lang.String.firstCharToUpperCase(field);
		obj.getClass().getMethod(setterName, value.getClass()).invoke(obj,value);
	}
	
    public static <T>  T attr(Object obj,String field) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
    	String getterName="get"+lava.lang.String.firstCharToUpperCase(field);
    	T t=null;
    	Object value=obj.getClass().getMethod(getterName).invoke(obj);
    	if(value!=null)t=(T)value;
		return t;
	}
	
}
