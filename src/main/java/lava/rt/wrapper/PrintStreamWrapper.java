package lava.rt.wrapper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

public class PrintStreamWrapper extends BaseWrapper<PrintStream>{

	

	
	public PrintStreamWrapper(PrintStream printStream) {
		super(printStream);
		// TODO Auto-generated constructor stub
	}


	public void printlnAsJson(Object...values ) {
		if(values.length==1) {
			toJsonObject(values[0]);
		}else if(values.length>1){
			toJsonArray(values);
		}
	}


	private String toJsonObject(Object object) {
		return null;
		// TODO Auto-generated method stub
		
	}
	
	
	private String toJsonArray(Object[] object) {
		return null;
		// TODO Auto-generated method stub
		
	}
}
