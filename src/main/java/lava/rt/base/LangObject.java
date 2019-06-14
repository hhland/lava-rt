package lava.rt.base;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lava.rt.common.ReflectCommon;


public abstract class LangObject {

	
	protected abstract Class<? extends LangObject> thisClass();

	
	protected final static Map<Class<? extends LangObject>,Map<String,Field>> CLS_FIELD_MAP=new HashMap<>();
	
	
	

	
	
	
	protected Map<String,Field> getFieldMap(){
		Map<String,Field> ret=null;
		if(CLS_FIELD_MAP.containsKey(thisClass())) {
			ret=CLS_FIELD_MAP.get(thisClass());
		}else {
			ret=ReflectCommon.allDeclaredFieldMap(thisClass());
			CLS_FIELD_MAP.put(thisClass(), ret);
		}
		return ret;
	}
	
	public <T> T val(String fieldName) throws NoSuchFieldException{
		Object ret=null;
		Map<String, Field> fieldMap=getFieldMap();
		if(!fieldMap.containsKey(fieldName)) throw new NoSuchFieldException();
		Field field=fieldMap.get(fieldName);
		field.setAccessible(true);
		try {
			ret=field.get(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {}
		return (T)ret;
	}
	
	public  void val(String fieldName,Object value) throws NoSuchFieldException{
		
		Map<String, Field> fieldMap=getFieldMap();
		if(!fieldMap.containsKey(fieldName)) throw new NoSuchFieldException();
		Field field=fieldMap.get(fieldName);
		field.setAccessible(true);
		try {
			field.set(this,value);
		} catch (IllegalArgumentException | IllegalAccessException e) {}
		
	}
	
    public int fromString(String value) {
    	int ret=0;
    	Pattern pattern= Pattern.compile(this.getClass().getSimpleName()+" [*]");
    	Matcher matcher=pattern.matcher(value);
    	if(!matcher.find())return ret;
    	String v= matcher.group(0);
    	return ret;
	}
	
	@Override
	public String toString() {
		StringBuffer sbr=new StringBuffer(this.thisClass().getSimpleName());
		sbr.append(" [");
		
		for(java.util.Map.Entry<String, Field> ent: getFieldMap().entrySet()) {
			Object val="null";
			try {
				Field field=ent.getValue();
				field.setAccessible(true);
				val = field.get(this).toString().replaceAll(",", "");
				
			} catch (Exception e) {}
			sbr
			.append(ent.getKey())
			.append("=")
			.append(val)
			.append(",")
			;
		}
		
		sbr.append("]");
		return sbr.toString();
	}

	
}
