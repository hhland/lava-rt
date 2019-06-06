package lava.rt.lang;

import java.lang.reflect.Field;
import java.util.Map;

import lava.rt.common.ReflectCommon;


public abstract class BaseObject {

	
	protected abstract Class<? extends BaseObject> thisClass();

	protected final Map<String,Field> fieldMap=ReflectCommon.getDeclaredFields(thisClass());
	
	@Override
	public String toString() {
		StringBuffer sbr=new StringBuffer(this.thisClass().getSimpleName());
		sbr.append(" [");
		
		for(java.util.Map.Entry<String, Field> ent: fieldMap.entrySet()) {
			String val="null";
			try {
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
