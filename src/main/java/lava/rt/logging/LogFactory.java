package lava.rt.logging;

import java.util.HashMap;
import java.util.Map;

import lava.rt.connectionpool.impl.AsyncServerStatus;

public class LogFactory {

	
	private static Map<Class,Log> logMap=new HashMap<>();
	
	public static Log getLog(Class cls) {
		// TODO Auto-generated method stub
		Log ret=null;
		if(logMap.containsKey(cls)) {
			ret=logMap.get(cls);
		}else {
			ret=new Log(cls);
			logMap.put(cls, ret);
		}
		
		return ret;
	}

}
