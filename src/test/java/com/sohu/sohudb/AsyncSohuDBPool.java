package com.sohu.sohudb;


import lava.rt.aio.tcp.TcpGenericConnectionPool;
import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;

public class AsyncSohuDBPool extends TcpGenericConnectionPool {

    public AsyncSohuDBPool(lava.rt.aio.tcp.TcpServerConfig serverConfig) {
        super(new AsyncSohuDBClientFactory(), serverConfig);

        this.setAutoSwitchToNextServer(false);
    }

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return LogFactory.SYSTEM.getLog(AsyncSohuDBPool.class);
	}

   

  

	
}
