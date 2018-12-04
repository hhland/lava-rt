package lava.rt.async;


import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.sql.ClientInfoStatus;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public abstract  class  AsyncConnectionPool<R extends AsyncRequest> {

	
	ExecutorService pool=Executors.newFixedThreadPool(2);
	
 	protected Selector selector;
	protected SelectableChannel channel;
 	protected Thread receiver=new Receiver(),sender=new Sender();
	
	public AsyncConnectionPool(SelectableChannel channel) throws IOException {
		super();
		selector=Selector.open();
		this.channel=channel;
		
	}


	public int sendRequest(R request) throws IOException {
		SelectionKey key = channel.register(selector,SelectionKey.OP_READ);
		key.attach(request);
		return key.readyOps();
	}

	
	
	public void run() throws IOException {
		pool.execute(receiver);
		pool.execute(sender);
		
		
	}
	
	
    public void stop() throws IOException {
		
		pool.shutdown();
		
	}
	
	
	protected class Receiver extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				  int readyChannels=0;
				try {
					readyChannels = selector.select();
				
				  if(readyChannels == 0) continue;
				  
				  Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
				  while(keyIterator.hasNext()) {
				    SelectionKey key = keyIterator.next();
				    R request=(R)key.attachment();
				    AsyncQueryClient client=request.getClient();
				    if (key.isReadable()&&request.isValid()) {
				        // a channel is ready for readin
				    	client.reset();
						client.handleInput(key);
						client.finishResponse();
				    }
				    
				    	keyIterator.remove();	
				    
				    
				  }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					  
				}
				  
				}
		}
		
	}
	
	
	protected class Sender extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				  int readyChannels=0;
				try {
					readyChannels = selector.select();
				
				  if(readyChannels == 0) continue;
				  Set selectedKeys = selector.selectedKeys();
				  Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				  while(keyIterator.hasNext()) {
				    SelectionKey key = keyIterator.next();
				    R request=(R)key.attachment(); 
				    if (key.isWritable()&&request.isValid()) {
				        // a channel is ready for writing
				    	 
					     request.getClient().sendRequest(key.channel());	
				    }
				    keyIterator.remove();
				  }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					  
				}
				  
				}
		}
	}
	
}
