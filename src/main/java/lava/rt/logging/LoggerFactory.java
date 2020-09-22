package lava.rt.logging;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.stream.Stream;

import lava.rt.aio.tcp.TcpServerStatus;

public class LoggerFactory {

	protected PrintStream[] infoStreams,errStreams,debugStreams
			;
	
	
	
	public static final LoggerFactory SYSTEM=new LoggerFactory();
	
	
	
	private LoggerFactory(){
		infoStreams=new PrintStream[] {System.out};
		debugStreams=new PrintStream[] {System.out};
				errStreams=new PrintStream[] {System.err}
				;
	}
	
	LoggerFactory(Properties properties){
		init(properties);
	}
	
	private  Map<Class,Logger> logMap=new HashMap<>();

	public Level level=Level.WARNING;
	

	
	public  Logger getLogger(Class cls) {
		// TODO Auto-generated method stub
		Logger ret=null;
		if(logMap.containsKey(cls)) {
			ret=logMap.get(cls);
			
		}else {
			ret=new Logger(this,cls);
			logMap.put(cls, ret);
		}
		
		return ret;
	}

	
	
	protected void init(Properties properties) {
		
	}
}
