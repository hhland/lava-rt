package lava.rt.base;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


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
	
	
	@Override
	public String toString() {
		StringBuffer sbr=new StringBuffer(this.thisClass().getSimpleName());
		sbr.append(" [");
		
		for(java.util.Map.Entry<String, Field> ent: getFieldMap().entrySet()) {
			Object val="null";
			try {
				Field field=ent.getValue();
				field.setAccessible(true);
				val = field.get(this);
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
