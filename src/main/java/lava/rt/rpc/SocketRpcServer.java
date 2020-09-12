package lava.rt.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketRpcServer extends RpcServer{

	private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	 
    
 
    
 
    protected final SocketAddress address;
    
    protected final ServerSocket  server  ;
 
    public SocketRpcServer(SocketAddress address) throws IOException {
        this.address = address;
        server = new ServerSocket();
        server.bind(address);
    }
    
    public void shutdown()  {
    	try {
    		executor.shutdown();
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
    public void pause() {
        isRunning = false;
        
    }
 
    public void start() throws IOException {
        
        
        System.out.println("start server: "+ address);
        
            while (!executor.isShutdown()) {
                // 1.监听客户端的TCP连接，接到TCP连接后将其封装成task，由线程池执行
            	if(isRunning) {
                   executor.execute(new ServiceTask(server.accept()));
                }
            }
        
    }
 
    
 
    
 
    
 
    private final class ServiceTask implements Runnable {
        Socket clent = null;
 
        public ServiceTask(Socket client) {
            this.clent = client;
        }
 
        public void run() {
            
            try (
            		ObjectInputStream input = new ObjectInputStream(clent.getInputStream());
                    ObjectOutputStream output = new ObjectOutputStream(clent.getOutputStream());
            		){
                // 2.将客户端发送的码流反序列化成对象，反射调用服务实现者，获取执行结果
                
                String serviceName = input.readUTF();
                String methodName = input.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                Object[] arguments = (Object[]) input.readObject();
                Object service = serviceRegistry.get(serviceName);
               
                Method method = service.getClass().getMethod(methodName, parameterTypes);
                synchronized(service) {
                  
                  Object result = method.invoke(service, arguments);
                  // 3.将执行结果反序列化，通过socket发送给客户端
                  
                  output.writeObject(result);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                
                if (clent != null) {
                    try {
                        clent.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
 
        }
    }

	
	
}
