package lava.rt.test.rpc;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SampleServiceImpl extends UnicastRemoteObject implements SampleService {

	protected SampleServiceImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String say(String word) {
		// TODO Auto-generated method stub
		return "hello:"+word;
	}

}
