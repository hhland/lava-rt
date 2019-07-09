package com.sohu.sohudb;

import lava.rt.aio.ClientFactory;

import lava.rt.aio.tcp.TcpQueryClient;
import lava.rt.aio.tcp.TcpServerConfig;
import lava.rt.logging.LogFactory;

public class AsyncSohuDBClientFactory implements ClientFactory<TcpQueryClient> {

	public TcpQueryClient newInstance() {
		return new AsyncSohuDBClient();
	}

	
	
	
	
	public static void main(String[] args) throws Exception {
		
		AsyncSohuDBClientFactory factory=new AsyncSohuDBClientFactory();
		
		TcpQueryClient client=factory.newInstance();
		
		LogFactory.SYSTEM.level=LogFactory.LEVEL_WARN;
		
		TcpServerConfig config=new TcpServerConfig("test", "hhlin@localhost:8080");
		
		AsyncSohuDBPool pool=new AsyncSohuDBPool(config);
		
		pool.init();
		
		
		
	}
	
}
