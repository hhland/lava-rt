package lava.rt.common;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import lava.rt.linq.sql.src.DataContextSrcGener;

public class LoggingCommon {
	
	public static final Logger CONSOLE=Logger.getGlobal();
	
	static{
		CONSOLE.addHandler(new ConsoleHandler());
		CONSOLE.setLevel(Level.ALL);
	}
	

	public static Logger getLogger(Class cls) {
		// TODO Auto-generated method stub
		Logger ret=Logger.getLogger(cls.getName());
		return ret;
	}

}
