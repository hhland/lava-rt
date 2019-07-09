package com.sohu.sohudb;

import lava.rt.aio.Request;
import lava.rt.aio.tcp.TcpGenericConnectionPool;
import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;

public class AsyncSohuDBPool extends TcpGenericConnectionPool {

    public AsyncSohuDBPool(String name) {
        super(new AsyncSohuDBClientFactory(), name);

        this.setAutoSwitchToNextServer(false);
    }

    public AsyncSohuDBPool() {
        this("SohuDB");
    }

    private static final Log logger = LogFactory.SYSTEM.getLog(AsyncSohuDBPool.class);

    protected Log getLogger() {
        return logger;
    }

	
}
