package com.sohu.sohudb;

import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;
import lava.rt.pool.Request;
import lava.rt.pool.impl.AsyncGenericConnectionPool;

public class AsyncSohuDBPool extends AsyncGenericConnectionPool {

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
