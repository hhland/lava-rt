package lava.rt.test.rpc;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import lava.rt.rpc.RpcServer;
import lava.rt.rpc.aio.AioRpcServer;
import lava.rt.rpc.nio.NioRpcServer;
import lava.rt.rpc.oio.SocketRpcServer;
import lava.rt.rpc.rmi.RmiRpcServer;

public class RpcServerMain {

	
	static SampleService impl;
	
	public enum Server{
		
		nio(new NioRpcServer(port)),aio(new AioRpcServer(port)),oio(new SocketRpcServer(port) );
		
		final RpcServer server;

		private Server(RpcServer server) {
			this.server = server;
		}

		public RpcServer getServer() {
			return server;
		}
		
		
		
	}
	
	
	public static int port=6000;

	public static void main(String[] args) throws RemoteException, Exception {
		// TODO Auto-generated method stub
		impl=new SampleServiceImpl();
		startNioServer();
		//startAioServer();
		
		
	}

	private static void startNioServer() throws Exception{
		// TODO Auto-generated method stub
		 
		 Server.nio.getServer().registerService(SampleService.class, impl);
		 Server.nio.getServer().start();
	}

	private static void startAioServer() throws Exception {
		// TODO Auto-generated method stub
       
		
		Server.aio.getServer().registerService(SampleService.class,impl);
		Server.aio.getServer().start();
	}

	
	private static void startOioServer() throws Exception {
		// TODO Auto-generated method stub
        
		
		Server.oio.getServer().registerService(SampleService.class,impl);
		Server.oio.getServer().start();
	}
	
}
