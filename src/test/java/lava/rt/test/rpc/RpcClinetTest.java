package lava.rt.test.rpc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import lava.rt.rpc.RpcClient;
import lava.rt.rpc.aio.AioRpcClient;
import lava.rt.rpc.nio.NioRpcClient;
import lava.rt.rpc.nio.NioRpcServer;
import lava.rt.rpc.oio.SocketRpcClient;
import lava.rt.rpc.rmi.RmiRpcClient;

public class RpcClinetTest {

	RpcClient client;
	
	
	
	public RpcClinetTest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
		
			
		 //client=new NioRpcClient("127.0.0.1", RpcServerMain.port);
	        
		client=new NioRpcClient("127.0.0.1", RpcServerMain.port);
	}
	
	//@Test
	public void testClinet() throws Exception {
		
		SampleService service=client.getService(SampleService.class);
		String ret=service.say("叼你");
		System.out.print(ret);
	}
	
}
