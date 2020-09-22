package lava.rt.logging;

import java.io.PrintStream;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

public class Logger {

	final Class cls;
	final LoggerFactory factory;
	
	
	
	
	
	final static SimpleDateFormat SDF_YMDHMS=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	protected Logger(LoggerFactory factory, Class cls) {
		// TODO Auto-generated constructor stub
		this.cls=cls;
		this.factory=factory;
		
	}

	
	public void warn(Object... vals) {
		// TODO Auto-generated method stub
		//factory.infoStream.forEach(s->{
		if(factory.level.intValue()>Level.WARNING.intValue()) return;
		for(PrintStream s : factory.infoStreams) {  
		       s.print(prefix("WARN"));
		       if(vals.length==1) {
		    	   s.print(vals[0]);
		       }else if(vals.length>1) {
		    	   s.print(join(vals));
		       }
		       s.println(join(vals));
		}
	   
	}

	

	public void info(Object... vals) {
		// TODO Auto-generated method stub
		//factory.infoStream.forEach(s->{
		if(factory.level.intValue()>Level.INFO.intValue()) return;
		for(PrintStream s : factory.infoStreams) {  	  
		       s.print(prefix("INFO"));
		       if(vals.length==1) {
		    	   s.print(vals[0]);
		       }else if(vals.length>1) {
		    	   s.print(join(vals));
		       }
		       s.println(join(vals));
		}
	    
	}

	public void error(Exception ex) {
		// TODO Auto-generated method stub
		//factory.errStream.forEach(s->{
		if(factory.level.intValue()>=Level.OFF.intValue()) return;
		for(PrintStream s : factory.infoStreams) {     
	       ex.printStackTrace(s);
		}
	   // );
	}
	

	public void error(Object... vals) {
		// TODO Auto-generated method stub
		//factory.errStream.forEach(s->{
		if(factory.level.intValue()>=Level.OFF.intValue()) return;
		for(PrintStream s : factory.infoStreams) {  
	       s.print(prefix("ERROR"));
	       if(vals.length==1) {
	    	   s.print(vals[0]);
	       }else if(vals.length>1) {
	    	   s.print(join(vals));
	       }
	       s.println(join(vals));
		}
	    
	}

	public static String join(Object... vals) {
		String[] ret=new String[vals.length];
		for(int i=0;i<vals.length;i++) {
			ret[i]=vals[i]==null?"":vals[i].toString();
		}
		return String.join(",", ret);
	}

	
	private String prefix(String type) {
		//2019-06-24 10:59:44.832  INFO 15216 --- [askScheduler-21] c.n.j.s.schedule.impl.JcjkAfter06Task:
		StringBuffer ret=new StringBuffer("\n");
		Thread t=Thread.currentThread();
		ret
		.append(SDF_YMDHMS.format(Calendar.getInstance().getTime()))
		.append(" ")
		.append(type).append(" ")
		.append(t.getId())
		.append(" --- [")
		.append(t.getName())
		.append("] ")
		.append(cls.getName())
		.append(":")
		;
		return ret.toString();
	}


	public void debug(Object... vals) {
		// TODO Auto-generated method stub
		info(vals);
	}
	
}
