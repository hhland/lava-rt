package lava.rt.rpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import lava.rt.wrapper.LoggerWrapper;

public abstract class RpcServer {

	
	    protected LoggerWrapper logger=LoggerWrapper.CONSOLE;
	
	    protected  boolean isRunning = true;
	
	    public void pause() {
	    	isRunning=false;
	    }
	    
	    public void resume() {
	    	isRunning=true;
	    }
	 
	    public abstract void start() throws IOException;
	 
	    public abstract <T,I extends T> void  registerService (Class<T> serviceInterface, I impl) throws Exception;
	    
	    
	    
	    
	    protected void invoke(ObjectInputStream input,ObjectOutputStream output) throws Exception {
			
			String serviceName = input.readUTF();
	        String methodName = input.readUTF();
	        Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
	        Object[] arguments = (Object[]) input.readObject();
	        Object service = Class.forName(serviceName);
	       
	        Method method = service.getClass().getMethod(methodName, parameterTypes);
	        
	        Object result = method.invoke(service, arguments);
	          // 3.将执行结果反序列化，通过socket发送给客户端
	          
	        output.writeObject(result);
	        
		}
	    
	 
	    protected byte[] toBytes(Object value) throws IOException {
			// TODO Auto-generated method stub
		final byte[] bytes;
	    try (
	    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);){
	    objectOutputStream.writeObject(value);
	    bytes = outputStream.toByteArray();
	    }
	    
	    return bytes;
		}
	 
	    
	
}
