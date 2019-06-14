package lava.rt.logging;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import lava.rt.pool.impl.AsyncServerStatus;

public class LogFactory {

	protected Stream<PrintStream> infoStream,errStream
			;
	
	public static final LogFactory SYSTEM=new LogFactory();
	
	private LogFactory(){
		infoStream=Stream.of(System.out).parallel();
				errStream=Stream.of(System.err).parallel()
				;
	}
	
	LogFactory(Properties properties){
		init(properties);
	}
	
	private  Map<Class,Log> logMap=new HashMap<>();
	
	public  Log getLog(Class cls) {
		// TODO Auto-generated method stub
		Log ret=null;
		if(logMap.containsKey(cls)) {
			ret=logMap.get(cls);
		}else {
			ret=new Log(this,cls);
			logMap.put(cls, ret);
		}
		
		return ret;
	}

	
	protected void init(Properties properties) {
		
	}
}
