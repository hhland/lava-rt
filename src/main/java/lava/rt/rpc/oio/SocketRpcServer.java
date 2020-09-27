package lava.rt.rpc.oio;

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

import lava.rt.rpc.RpcServer;

public class SocketRpcServer extends RpcServer{

	private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	 
    
 
    
 
    protected final SocketAddress address;
    
    protected  ServerSocket  server  ;





	public Map<String,Object> serviceMap=new HashMap<>();
 
	
	public SocketRpcServer(int port)  {
        this.address = new InetSocketAddress(port);
       
    }
	
    public SocketRpcServer(SocketAddress address) {
        this.address = address;
        
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
    	 server = new ServerSocket();
         server.bind(address);
        
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
                invoke(input, output);              
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







	@Override
	public <T, I extends T> void registerService(Class<T> serviceInterface, I impl) {
		// TODO Auto-generated method stub
		serviceMap.put(serviceInterface.getName(), impl);
	}

	
	
}
