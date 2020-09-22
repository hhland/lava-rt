package lava.rt.rpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class RpcServer {

	
	    
	
	    protected  boolean isRunning = true;
	
	    public void pause() {
	    	isRunning=false;
	    }
	    
	    public void resume() {
	    	isRunning=true;
	    }
	 
	    public abstract void start() throws IOException;
	 
	    public abstract <T,I extends T> void  registerService (Class<T> serviceInterface, I impl) throws Exception;
	    
	    
	 
	  
	 
	    
	
}
