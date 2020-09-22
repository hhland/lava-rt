package lava.rt.test.rpc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import lava.rt.rpc.rmi.RmiRpcClient;

public class RpcClinetTest {

	RmiRpcClient client;
	
	public RpcClinetTest() {
		try {
			client=new RmiRpcClient(InetAddress.getByName("localhost"),6000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testClinet() throws Exception {
		
		SampleService service=client.getProxy(SampleService.class);
		String ret=service.say("叼你");
		System.out.print(ret);
	}
	
}
