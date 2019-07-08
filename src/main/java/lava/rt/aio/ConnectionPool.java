package lava.rt.aio;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Set;

import lava.rt.aio.async.AsyncGenericQueryClient;

public abstract class ConnectionPool  {

	
	protected Selector selector;
	
    
    
	public Selector wakeup() {
		return selector.wakeup();
	}
   
	
	public  void  registe(AbstractSelectableChannel channel,int option,Object client) throws ClosedChannelException {
		channel.register(selector, option, client);
	}
	
	
	public int select(long robinTime) throws IOException {
		int ret = selector.select(robinTime);
		return ret;
	}
	
	
	public Set<SelectionKey> selectedKeys() {
		return selector.selectedKeys();
	}
}
