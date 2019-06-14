package lava.rt.logging;



public class Log<E> {

	final Class<E> cls;
	final LogFactory factory;
	
	
	
	
	
	protected Log(LogFactory factory, Class<E> cls) {
		// TODO Auto-generated constructor stub
		this.cls=cls;
		this.factory=factory;
	}

	
    

	

	public void info(Object... vals) {
		// TODO Auto-generated method stub
		factory.infoStream.forEach(s->
	       
	       s.println(join(vals))
	    
	    );
	}

	public void error(Exception ex) {
		// TODO Auto-generated method stub
		factory.errStream.forEach(s->
	       
	       ex.printStackTrace(s)
	    
	    );
	}
	

	public void error(Object... vals) {
		// TODO Auto-generated method stub
		factory.errStream.forEach(s->
	       
	       s.println(join(vals))
	    
	    );
	}

	public static String join(Object... vals) {
		String[] ret=new String[vals.length];
		for(int i=0;i<vals.length;i++) {
			ret[i]=vals[i]==null?"":vals[i].toString();
		}
		return String.join(",", ret);
	}

}
