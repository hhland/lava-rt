package lava.rt.rpc;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RmiRpcClient extends RpcClient{

	String urlRoot;
	
	public RmiRpcClient(InetAddress addr,int port) {
		
		// TODO Auto-generated constructor stub
		urlRoot="rmi://"+addr.getAddress()+":"+port+"/";
	}

	@Override
	public <T> T getProxy(Class<T> serviceInterface) throws Exception {
		// TODO Auto-generated method stub
		String url=urlRoot+serviceInterface.getName();
		T ret =(T) Naming.lookup(url); 
		return ret;
	}

}
