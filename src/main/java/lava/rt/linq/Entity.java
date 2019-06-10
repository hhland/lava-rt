package lava.rt.linq;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import lava.rt.base.LangObject;
import lava.rt.common.ReflectCommon;

public abstract class Entity extends LangObject  {

	
     
	protected Date _createTime,_updateTime;
     
     
     


	@Override
	public String toString() {
		String ret= "Entity [_createTime=" + _createTime + ", _updateTime=" + _updateTime + "]";
		ret += super.toString();
		return ret;
		
	}


	public Date get_createTime() {
		return _createTime;
	}


	public Date get_updateTime() {
		return _updateTime;
	}
     
     
    
	
	
}
