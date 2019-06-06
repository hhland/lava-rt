package lava.rt.linq;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import lava.rt.common.ReflectCommon;
import lava.rt.lang.BaseObject;

public abstract class Entity extends BaseObject {

	
     public Date _createTime,_updateTime;
     
     
     Entity(){
    	 
    	 this.fieldMap.remove("_createTime");
    	 this.fieldMap.remove("_updateTime");
     }


	@Override
	public String toString() {
		String ret= "Entity [_createTime=" + _createTime + ", _updateTime=" + _updateTime + "]";
		ret += super.toString();
		return ret;
		
	}
     
     
     
}
