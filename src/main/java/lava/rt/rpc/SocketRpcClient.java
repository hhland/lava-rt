package lava.rt.rpc;


import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;



public class SocketRpcClient extends RpcClient{

	
	
	
	
	
	public SocketRpcClient(InetSocketAddress addr) {
		super(addr);
		
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

	
	
	
}
