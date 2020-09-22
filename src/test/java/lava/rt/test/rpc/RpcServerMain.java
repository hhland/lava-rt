package lava.rt.test.rpc;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import lava.rt.rpc.rmi.RmiRpcServer;

public class RpcServerMain {

	
	public static void main(String[] args) throws RemoteException, Exception {
		// TODO Auto-generated method stub

		RmiRpcServer service=new RmiRpcServer(6000);
		
		SampleServiceImpl impl=new SampleServiceImpl();
		service.registerService(SampleService.class,impl);
		
		while(true) {
			
		}
	}

}
