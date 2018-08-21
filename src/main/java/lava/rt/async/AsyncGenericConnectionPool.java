package lava.rt.async;


import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

public abstract  class  AsyncGenericConnectionPool<R extends AsyncRequest> {

 	protected Selector selector;
	protected SelectableChannel channel;
 	protected Thread receiver=new Receiver(),sender=new Sender();
	
	public AsyncGenericConnectionPool(SelectableChannel channel) throws IOException {
		super();
		selector=Selector.open();
		this.channel=channel;
		receiver.start();
		sender.start();
	}


	public int sendRequest(R request) throws IOException {
		SelectionKey key = channel.register(selector,SelectionKey.OP_READ);
		key.attach(request);
		return key.readyOps();
	}

	
	
	public void run() throws IOException {
		
		
		
	}
	
	
	protected class Receiver extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				  int readyChannels=0;
				try {
					readyChannels = selector.select();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  if(readyChannels == 0) continue;
				  Set selectedKeys = selector.selectedKeys();
				  Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				  while(keyIterator.hasNext()) {
				    SelectionKey key = keyIterator.next();
				    if (key.isReadable()) {
				        // a channel is ready for reading
				    }
				    keyIterator.remove();
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
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  if(readyChannels == 0) continue;
				  Set selectedKeys = selector.selectedKeys();
				  Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
				  while(keyIterator.hasNext()) {
				    SelectionKey key = keyIterator.next();
				     if (key.isWritable()) {
				        // a channel is ready for writing
				    	
				    }
				    keyIterator.remove();
				  }
				}
		}
	}
	
}
