package com.sohu.sohudb;

import lava.rt.aio.async.AsyncClientFactory;
import lava.rt.aio.async.AsyncGenericQueryClient;

public class AsyncSohuDBClientFactory implements AsyncClientFactory {

	public AsyncGenericQueryClient newInstance() {
		return new AsyncSohuDBClient();
	}

	
	
	
	
	public static void main(String[] args) throws Exception {
		
		AsyncSohuDBClientFactory factory=new AsyncSohuDBClientFactory();
		
		AsyncGenericQueryClient client=factory.newInstance();
		
		AsyncSohuDBPool pool=new AsyncSohuDBPool();
		
		pool.init();
		
		
		
	}
	
}
