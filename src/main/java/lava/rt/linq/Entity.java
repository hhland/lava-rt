package lava.rt.linq;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lava.rt.base.LangObject;
import lava.rt.common.ReflectCommon;

public abstract class Entity extends LangObject implements Cloneable  {

	
	protected Date newAt,updateAt;

     
    public Entity() {
		newAt=now();
	} 


	



	protected static Date now() {
		// TODO Auto-generated method stub
		return Calendar.getInstance().getTime();
	}







	public static <E extends Entity> E[] newEntitys(int size,Class<E> entryClass,Object ...objects) throws Exception {
		E[] ret=(E[])Array.newInstance(entryClass,size);
		for(int i=0;i<ret.length;i++){
			ret[i]=newEntity(entryClass, objects);
		}
		return ret;
	}
	
	
	public static <E extends Entity> E newEntity(Class<E> entryClass,Object ...objects) throws Exception {
		E ret = null;
		if(objects.length==0){
		   ret= unsafeAdapter.allocateInstance(entryClass);
		}else{
		   ret = (E) entryClass.getConstructors()[0].newInstance(objects);
		}
		return ret;
	}

	

	
}
