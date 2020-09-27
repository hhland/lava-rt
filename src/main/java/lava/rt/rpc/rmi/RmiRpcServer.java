package lava.rt.rpc.rmi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

import lava.rt.rpc.RpcServer;

public class RmiRpcServer extends RpcServer{

	
    Registry registry = null;
    
    public RmiRpcServer(int port) throws RemoteException {
		registry=LocateRegistry.createRegistry(port);
    }


	@Override
	public <T, I extends T> void registerService(Class<T> serviceInterface, I impl) throws Exception {
		// TODO Auto-generated method stub
		
		if(!(impl instanceof UnicastRemoteObject)) {
			throw new Exception("impl 必须实现 "+UnicastRemoteObject.class.getName());
		}
		UnicastRemoteObject uro=(UnicastRemoteObject)impl;
		registry.bind(serviceInterface.getName(), uro);
	}
	
	
	



	@Override
	public void start() throws IOException {
		// TODO Auto-generated method stub
		
	}
	
    
    
	
}
