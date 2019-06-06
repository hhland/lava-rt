package lava.rt.base;

import java.lang.reflect.Field;
import java.util.Map;

import lava.rt.common.ReflectCommon;


public abstract class LangObject {

	
	protected abstract Class<? extends LangObject> thisClass();

	protected final Map<String,Field> fieldMap=ReflectCommon.getDeclaredFieldMap(thisClass());
	
	@Override
	public String toString() {
		StringBuffer sbr=new StringBuffer(this.thisClass().getSimpleName());
		sbr.append(" [");
		
		for(java.util.Map.Entry<String, Field> ent: fieldMap.entrySet()) {
			String val="null";
			try {
				ent.getValue().setAccessible(true);
				val = ent.getValue().get(this).toString();
			} catch (IllegalArgumentException | IllegalAccessException e) {}
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
