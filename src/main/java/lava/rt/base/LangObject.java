package lava.rt.base;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNotSame;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lava.rt.adapter.UnsafeAdapter;
import lava.rt.common.ReflectCommon;
import sun.misc.Unsafe;


public abstract class LangObject {

	
	protected abstract Class<? extends LangObject> thisClass();

	
	protected final static Map<Class<? extends LangObject>,Map<String,Field>> CLS_FIELD_MAP=new HashMap<>();
	
	//protected final static Map<Class<? extends LangObject>,Map<String,Long>> CLS_FIELDOFFSET_MAP=new HashMap<>();

	

	//protected static UnsafeAdapter unsafeAdapter=UnsafeAdapter.getInstance();
	
	
	protected Map<String,Field> getFieldMap(){
		Map<String,Field> ret=CLS_FIELD_MAP.get(thisClass());
		if(ret==null) {
			ret=ReflectCommon.allDeclaredFieldMap(thisClass());
			CLS_FIELD_MAP.put(thisClass(), ret);
		}
		return ret;
	}
	
//	protected Map<String,Long> getFieldOffsetMap(){
//		Map<String,Long> ret=CLS_FIELDOFFSET_MAP.get(thisClass());
//		if(ret==null) {
//			ret=unsafeAdapter.allDeclaredFieldOffsetMap(thisClass());
//			CLS_FIELDOFFSET_MAP.put(thisClass(), ret);
//		}
//		return ret;
//	}
	
	@SuppressWarnings("unchecked")
	public <T> T val(String fieldName) throws Exception {
		T ret=null;
		Map<String,Field> om=getFieldMap();
		ret=(T) om.get(fieldName).get(this);
		return ret;
	}
	
	public  void val(String fieldName,Object value) throws Exception {
		
		Map<String,Field> om=getFieldMap();
		om.get(fieldName).set(this, value);

	}
	

	
   
	public String format(String pattern) {
		String ret=pattern;
		Field[] fields=thisClass().getDeclaredFields();
		Object[] arguments=new Object[fields.length];
		//Map<String,Object> argumentMap=new HashMap<>();
		try {
		for(int i=0;i<fields.length;i++) {
			Field fieldi=fields[i];
			String fieldName=fieldi.getName();
			
			String val=val(fieldName)==null?"null":val(fieldName).toString();
			arguments[i]=val;
			
		    String el="${"+fieldName+"}";
			ret=ret.replace(el, val);	
		}
		}catch(Exception ex) {};
		ret=MessageFormat.format(pattern, arguments);
		return ret;
	}
	
	@Override
	public String toString() {
		StringBuffer sbr=new StringBuffer(this.thisClass().getSimpleName());
		sbr.append(" [");
		
		for(Entry<String, Field> ent: getFieldMap().entrySet()) {
			Object val="null";
			try {
				//Field field=ent.getValue();
				//field.setAccessible(true);
				val = this.val(ent.getKey()).toString().replace(",", " ");
				
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
