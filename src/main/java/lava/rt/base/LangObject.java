package lava.rt.base;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lava.rt.adapter.UnsafeAdapter;
import lava.rt.common.ReflectCommon;
import sun.misc.Unsafe;


public abstract class LangObject {

	
	protected abstract Class<? extends LangObject> thisClass();

	
	protected final static Map<Class<? extends LangObject>,Map<String,Field>> CLS_FIELD_MAP=new HashMap<>();
	
	protected final static Map<Class<? extends LangObject>,Map<String,Long>> CLS_FIELDOFFSET_MAP=new HashMap<>();

	@SuppressWarnings("restriction")
	protected static Unsafe unsafe=ReflectCommon.UNSAFE;

	protected static UnsafeAdapter unsafeAdapter=new UnsafeAdapter(unsafe);
	
	
	protected Map<String,Field> getFieldMap(){
		Map<String,Field> ret=CLS_FIELD_MAP.get(thisClass());
		if(ret==null) {
			ret=ReflectCommon.allDeclaredFieldMap(thisClass());
			CLS_FIELD_MAP.put(thisClass(), ret);
		}
		return ret;
	}
	
	protected Map<String,Long> getFieldOffsetMap(){
		Map<String,Long> ret=CLS_FIELDOFFSET_MAP.get(thisClass());
		if(ret==null) {
			ret=unsafeAdapter.allDeclaredFieldOffsetMap(thisClass());
			CLS_FIELDOFFSET_MAP.put(thisClass(), ret);
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("restriction")
	public <T> T val(Long offset) {
		Object ret=null;
		//Map<String, Field> fieldMap=getFieldMap();
		
		ret=unsafe.getObject(this, offset);
		
		return (T)ret;
	}
	
	@SuppressWarnings("restriction")
	public  void val(Long offset,Object value) {
		
		unsafe.putObject(this, offset, value);
		
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
