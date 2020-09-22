package lava.rt.test.rpc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SampleService extends Remote{

	
	
	public String say(String word) throws RemoteException; 
	
}
