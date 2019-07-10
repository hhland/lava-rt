package lava.rt.logging;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Stream;

import lava.rt.aio.tcp.TcpServerStatus;

public class LogFactory {

	protected PrintStream[] infoStreams,errStreams,debugStreams
			;
	
	
	
	public static final LogFactory SYSTEM=new LogFactory();
	
	
	
	private LogFactory(){
		infoStreams=new PrintStream[] {System.out};
		debugStreams=new PrintStream[] {System.out};
				errStreams=new PrintStream[] {System.err}
				;
	}
	
	LogFactory(Properties properties){
		init(properties);
	}
	
	private  Map<Class,Log> logMap=new HashMap<>();

	public Level level=Level.WARNING;
	

	
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
