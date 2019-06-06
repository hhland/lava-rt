package lava.rt.base;

import java.lang.reflect.Field;
import java.util.Map;

import lava.rt.adapter.UnsafeAdapter;
import lava.rt.common.ReflectCommon;


public abstract class LangObject {

	
	protected abstract Class<? extends LangObject> thisClass();

	protected final Map<String,Field> fieldMap=ReflectCommon.getDeclaredFieldMap(thisClass());
	
	protected static final  UnsafeAdapter unsafeAdapter=new UnsafeAdapter();
	
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

	
	public   boolean compareAndSwapObject(String fieldName, Object value) throws Exception {
	     
		 boolean ret=false;
		 ret=unsafeAdapter.compareAndSwapObject(this, fieldMap.get(fieldName),value);
		 return ret;
	}
	
	public   boolean compareAndSwapInt(String fieldName, int value) throws Exception {
		 boolean ret=false;
		 ret=unsafeAdapter.compareAndSwapInt(this, fieldMap.get(fieldName),value);
		 return ret;
	}
	
	public   boolean compareAndSwapLong(String fieldName, long value) throws Exception {
		boolean ret=false;
		 ret=unsafeAdapter.compareAndSwapLong(this, fieldMap.get(fieldName),value);
		 return ret;
	}
}
