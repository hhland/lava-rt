package lava.rt.rpc.nio;

import java.io.IOException;




import lava.rt.rpc.RpcServer;

public abstract  class NioRpcServer<REQ extends NaRPCMessage,RES extends NaRPCMessage> 
extends RpcServer implements NaRPCService<REQ, RES>{

	public NioRpcServer(int port) {
		super(port);
		// TODO Auto-generated constructor stub
	}
	
	public abstract NaRPCServerEndpoint<REQ, RES> createServerEndpoint();

	@Override
	public void start() throws IOException {
		// TODO Auto-generated method stub
		try {
			NaRPCServerEndpoint<REQ, RES> serverEndpoint = createServerEndpoint();
			serverEndpoint.bind(this.addr);			
			
			while(isRunning){
				NaRPCServerChannel endpoint = serverEndpoint.accept();
				System.out.println("new RPC connection, address " + endpoint.address());
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


}
