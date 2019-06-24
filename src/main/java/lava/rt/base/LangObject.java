package lava.rt.base;

import java.io.PrintStream;
import java.lang.reflect.Field;
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

	
	//protected final static Map<Class<? extends LangObject>,Map<String,Field>> CLS_FIELD_MAP=new HashMap<>();
	
	protected final static Map<Class<? extends LangObject>,Map<String,Long>> CLS_FIELDOFFSET_MAP=new HashMap<>();

	

	protected static UnsafeAdapter unsafeAdapter=UnsafeAdapter.getInstance();
	
	
//	protected Map<String,Field> getFieldMap(){
//		Map<String,Field> ret=CLS_FIELD_MAP.get(thisClass());
//		if(ret==null) {
//			ret=ReflectCommon.allDeclaredFieldMap(thisClass());
//			CLS_FIELD_MAP.put(thisClass(), ret);
//		}
//		return ret;
//	}
	
	protected Map<String,Long> getFieldOffsetMap(){
		Map<String,Long> ret=CLS_FIELDOFFSET_MAP.get(thisClass());
		if(ret==null) {
			ret=unsafeAdapter.allDeclaredFieldOffsetMap(thisClass());
			CLS_FIELDOFFSET_MAP.put(thisClass(), ret);
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T val(String fieldName) {
		T ret=null;
		Map<String,Long> om=getFieldOffsetMap();
		ret=val(om.get(fieldName));
		return ret;
	}
	
	public  void val(String fieldName,Object value) {
		
		Map<String,Long> om=getFieldOffsetMap();
		val(om.get(fieldName),value);

	}
	
	@SuppressWarnings("restriction")
	public <T> T val(Long offset) {
		Object ret=null;
		//Map<String, Field> fieldMap=getFieldMap();
		
		ret=unsafeAdapter.getObject(this, offset);
		
		return (T)ret;
	}
	
	@SuppressWarnings("restriction")
	public  void val(Long offset,Object value) {
		
		unsafeAdapter.putObject(this, offset, value);
		
	}
	
   
	
	@Override
	public String toString() {
		StringBuffer sbr=new StringBuffer(this.thisClass().getSimpleName());
		sbr.append(" [");
		
		for(Entry<String, Long> ent: getFieldOffsetMap().entrySet()) {
			Object val="null";
			try {
				//Field field=ent.getValue();
				//field.setAccessible(true);
				val = this.val(ent.getValue()).toString().replace(",", " ");
				
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
