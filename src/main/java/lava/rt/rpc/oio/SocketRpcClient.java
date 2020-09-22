package lava.rt.rpc.oio;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import lava.rt.rpc.RpcClient;



public class SocketRpcClient extends RpcClient{

	InetSocketAddress addr;
	
	protected final Map<Class, ProxyHandler> handlerMap = new HashMap<>();
	
	
	public SocketRpcClient(InetAddress addr,int port) {
		
		this.addr=new InetSocketAddress(addr,port);
	}   


	@SuppressWarnings("unchecked")
	public  <T> T getProxy(final Class<T> serviceInterface) {
		ProxyHandler proxyHandler=handlerMap.get(serviceInterface);
		if(proxyHandler==null) {
			proxyHandler=new ProxyHandler(serviceInterface);
			handlerMap.put(serviceInterface, proxyHandler);
		}
        // 1.将本地的接口调用转换成JDK的动态代理，在动态代理中实现接口的远程调用
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface},
                proxyHandler    
        		);
    } 

	
	
	protected class ProxyHandler implements InvocationHandler {

		final Class serviceInterface;

		public ProxyHandler(Class serviceInterface) {
			super();
			this.serviceInterface = serviceInterface;
			
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// TODO Auto-generated method stub
			Object ret=null;
			try (
					Socket socket = new Socket(addr.getAddress(),addr.getPort());
					ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
					){
				
				output.writeUTF(serviceInterface.getName());
				output.writeUTF(method.getName());
				output.writeObject(method.getParameterTypes());
				output.writeObject(args);

				ret=input.readObject();
			} 
			return ret;
		}

	}
}
