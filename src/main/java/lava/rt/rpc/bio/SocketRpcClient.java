package lava.rt.rpc.bio;


import java.io.IOException;
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

	
	
	protected final Map<Class, ProxyHandler> handlerMap = new HashMap<>();
	
	
	public SocketRpcClient(InetSocketAddress addr) throws IOException {
		super(addr);
	}
	
	public SocketRpcClient(String hostname,int port) throws IOException {
		super(new InetSocketAddress(hostname, port));
		
	}   


	@SuppressWarnings("unchecked")
	public  <T> T getService(final Class<T> serviceInterface) {
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

	
	
	



	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
