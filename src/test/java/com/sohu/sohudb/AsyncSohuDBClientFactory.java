package com.sohu.sohudb;

import lava.rt.aio.ClientFactory;

import lava.rt.aio.tcp.TcpGenericQueryClient;
import lava.rt.aio.tcp.TcpServerConfig;
import lava.rt.logging.LogFactory;

public class AsyncSohuDBClientFactory implements ClientFactory<TcpGenericQueryClient> {

	public TcpGenericQueryClient newInstance() {
		return new AsyncSohuDBClient();
	}

	
	
	
	
	public static void main(String[] args) throws Exception {
		
		AsyncSohuDBClientFactory factory=new AsyncSohuDBClientFactory();
		
		TcpGenericQueryClient client=factory.newInstance();
		
		LogFactory.SYSTEM.level=LogFactory.LEVEL_WARN;
		
		TcpServerConfig config=new TcpServerConfig("test", "hhlin@localhost:8080");
		
		AsyncSohuDBPool pool=new AsyncSohuDBPool(config);
		
		pool.init();
		
		
		
	}
	
}
