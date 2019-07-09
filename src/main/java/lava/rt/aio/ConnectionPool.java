package lava.rt.aio;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Set;

import lava.rt.aio.tcp.TcpQueryClient;
import lava.rt.aio.udp.UdpRequest;
import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;

public abstract class ConnectionPool<R>  {

	
	private Selector selector;
	
    
    
	public Selector wakeup() {
		return selector.wakeup();
	}
   
	
	public  SelectionKey  register(AbstractSelectableChannel channel,int option,Object client) throws ClosedChannelException {
		return channel.register(selector, option, client);
	}
	
	
	public int select(long robinTime) throws IOException {
		int ret = selector.select(robinTime);
		return ret;
	}
	
	
	public Set<SelectionKey> selectedKeys() {
		return selector.selectedKeys();
	}
	
	
	abstract public int sendRequest(R request );
	
	 /**
     * ��ü�¼��ʵ��
     * 
     * @return
     */
    protected  Log getLogger() {
    	return LogFactory.SYSTEM.getLog(ConnectionPool.class);
    }
    
    
    public void init() throws Exception {
    	selector=Selector.open();
    }
    
    public void destroy() throws IOException {
    	
			selector.close();
		
    }


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		destroy();
	}
    
    
    
}
