package lava.rt.rpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import lava.rt.wrapper.LoggerWrapper;

public abstract class RpcServer {

	
	    protected LoggerWrapper logger=LoggerWrapper.CONSOLE;
	
	    protected  boolean isRunning = true;
	    
	    protected InetSocketAddress addr ;
	    
	    protected final Map<String,Object> serviceMap=new HashMap<>();
	    
	    public RpcServer(int port) {
	    	addr=new InetSocketAddress(port);
	    }
	
	    public void pause() {
	    	isRunning=false;
	    }
	    
	    public void resume() {
	    	isRunning=true;
	    }
	 
	    public abstract void start() throws IOException;
	 
	    public   void  registerService (String name, Object impl) throws Exception{
	    	this.serviceMap.put(name, impl);
	    }
	    
	    public  <T,I extends T> void  registerService (Class<T> serviceInterface, I impl) throws Exception{
	    	this.serviceMap.put(serviceInterface.getName(), impl);
	    }
	    
	    
	    protected void invoke(ObjectInputStream input,ObjectOutputStream output) throws Exception {
			
			String serviceName = input.readUTF();
	        String methodName = input.readUTF();
	        Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
	        Object[] arguments = (Object[]) input.readObject();
	        Object service = serviceMap.get(serviceName);
	       
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
