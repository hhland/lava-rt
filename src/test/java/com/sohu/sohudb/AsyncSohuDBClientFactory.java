package com.sohu.sohudb;

import lava.rt.pool.impl.AsyncClientFactory;
import lava.rt.pool.impl.AsyncGenericQueryClient;

public class AsyncSohuDBClientFactory implements AsyncClientFactory {

	public AsyncGenericQueryClient newInstance() {
		return new AsyncSohuDBClient();
	}

}
