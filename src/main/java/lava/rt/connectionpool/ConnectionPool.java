package lava.rt.connectionpool;

import java.io.IOException;

import lava.rt.connectionpool.impl.AsyncClientFactory;
import lava.rt.connectionpool.impl.AsyncGenericConnectionPool;
import lava.rt.connectionpool.impl.AsyncGenericQueryClient;
import lava.rt.logging.Log;

public abstract class ConnectionPool {

	abstract int sendRequest(Request request); 
	
	
	
	public static void main(String[] args) {
		
		  AsyncClientFactory factory=new AsyncClientFactory() {
			
			@Override
			public AsyncGenericQueryClient newInstance() {
				// TODO Auto-generated method stub
				return new AsyncGenericQueryClient() {
					
					@Override
					public int sendRequest() throws IOException {
						// TODO Auto-generated method stub
						
						return 0;
					}
					
					@Override
					public void reset() throws IOException {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					protected int handleInput() throws IOException {
						// TODO Auto-generated method stub
						return 0;
					}
					
					@Override
					protected Log getLogger() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					protected boolean finishResponse() throws IOException {
						// TODO Auto-generated method stub
						return false;
					}
				};
			}
		};
		
		  ConnectionPool pool=new AsyncGenericConnectionPool(factory,"test") {
			
			@Override
			int sendRequest(Request request) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			protected Log getLogger() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
	}
	
	
}
