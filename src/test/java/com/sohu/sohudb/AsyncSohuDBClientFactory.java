package com.sohu.sohudb;

import lava.rt.aio.tcp.TcpClientFactory;
import lava.rt.aio.tcp.TcpGenericQueryClient;

public class AsyncSohuDBClientFactory implements TcpClientFactory {

	public TcpGenericQueryClient newInstance() {
		return new AsyncSohuDBClient();
	}

	
	
	
	
	public static void main(String[] args) throws Exception {
		
		AsyncSohuDBClientFactory factory=new AsyncSohuDBClientFactory();
		
		TcpGenericQueryClient client=factory.newInstance();
		
		AsyncSohuDBPool pool=new AsyncSohuDBPool();
		
		pool.init();
		
		
		
	}
	
}
