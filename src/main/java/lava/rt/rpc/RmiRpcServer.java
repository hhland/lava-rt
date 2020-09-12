package lava.rt.rpc;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

public class RmiRpcServer extends RpcServer{

	
    Registry registry = null;
    
    public RmiRpcServer(int port) throws RemoteException {
		registry=LocateRegistry.createRegistry(port);
    }

    
    
    public <T extends UnicastRemoteObject> void  registerService (T serviceImpl) throws AccessException, RemoteException, AlreadyBoundException {
        registry.bind(serviceImpl.getClass().getName(), serviceImpl);
    }



	@Override
	public void start() throws IOException {
		// TODO Auto-generated method stub
		
	}
	
    
    
	
}
