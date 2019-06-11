package lava.rt.rpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class RpcServer {

	
	    protected  final Map<String, Object> serviceRegistry = new HashMap<>();
	
	    protected  boolean isRunning = true;
	
	    public void pause() {
	    	isRunning=false;
	    }
	    
	    public void resume() {
	    	isRunning=true;
	    }
	 
	    public abstract void start() throws IOException;
	 
	    public <T> void  registerService (Class<T> serviceInterface, T impl) {
	        serviceRegistry.put(serviceInterface.getName(), impl);
	    }
	    
	    
	 
	  
	 
	    
	
}
