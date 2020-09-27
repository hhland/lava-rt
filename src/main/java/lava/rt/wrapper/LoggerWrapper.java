package lava.rt.wrapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;



public class LoggerWrapper extends BaseWrapper<Logger>{

	final static SimpleDateFormat SDF_YMDHMS=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String clsName="";
	
	public LoggerWrapper(Logger _this) {
		super(_this);
		
		// TODO Auto-generated constructor stub
	}

	public LoggerWrapper(Class cls) {
		super(Logger.getLogger(cls.getName()));
		clsName=cls.getName();
	}
	
	
    public static final LoggerWrapper CONSOLE=new LoggerWrapper(Logger.getGlobal());
	
	static{
		CONSOLE.self.addHandler(new ConsoleHandler());
		CONSOLE.self.setLevel(Level.INFO);
	}

	public void print(Object...values) {
		// TODO Auto-generated method stub
		self.log(Level.ALL,join(values));	  
	}
	
	public void info(Object...values) {
		// TODO Auto-generated method stub
		 //_this.log(Level.INFO,prefix("INFO"));
	     self.info(join(values));
		
	}

	public void warn(Object...values) {
		// TODO Auto-generated method stub
		
	     self.log(Level.WARNING,join(values));
	}
	

	private  static String join(Object... vals) {
		String[] ret=new String[vals.length];
		for(int i=0;i<vals.length;i++) {
			Object vali=vals[i];
			if(vali==null) {
				ret[i]="null";
			}else if(vali instanceof Throwable) {
			    Throwable tr=(Throwable)vali;
				ret[i]=new StringBuffer("[")
						.append(tr.getClass().getName()).append(":")
						.append(tr.getMessage())
						.append("]").toString();	
			}else {
				ret[i]=vali.toString();
			}
			
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
		.append(clsName)
		.append(":")
		;
		return ret.toString();
	}
}
