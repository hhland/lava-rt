package lava.rt.rpc.rmi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lava.rt.rpc.RpcClient;

public class RmiRpcClient extends RpcClient{

	String urlRoot;
	
	public RmiRpcClient(String hostname,int port) throws IOException {
		super(new InetSocketAddress(hostname, port));
		// TODO Auto-generated constructor stub
		urlRoot="rmi://"+addr.getHostName()+":"+port+"/";
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(Class<T> serviceInterface) throws Exception {
		// TODO Auto-generated method stub
		String url=urlRoot+serviceInterface.getName();
		T ret =(T) Naming.lookup(url); 
		return ret;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
