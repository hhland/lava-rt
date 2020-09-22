package lava.rt.rpc.rmi;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lava.rt.rpc.RpcClient;

public class RmiRpcClient extends RpcClient{

	String urlRoot;
	
	public RmiRpcClient(InetAddress addr,int port) {
		
		// TODO Auto-generated constructor stub
		urlRoot="rmi://"+addr.getHostAddress()+":"+port+"/";
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProxy(Class<T> serviceInterface) throws Exception {
		// TODO Auto-generated method stub
		String url=urlRoot+serviceInterface.getName();
		T ret =(T) Naming.lookup(url); 
		return ret;
	}

}
