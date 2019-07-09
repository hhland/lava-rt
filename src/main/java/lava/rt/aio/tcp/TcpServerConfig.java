package lava.rt.aio.tcp;

public class TcpServerConfig {

	
	/// �������ٴδ����,����sleep����
	protected final int maxErrorsBeforeSleep = 4
	         ,sleepMillisecondsAfterTimeOutError = 30000
	          , maxConnectionsPerServer = 8
	          ,maxClonedRequest = 2
	          ,maxQueueSize = 10000
	        	,	  maxResponseRadio = 5
	          ;
	protected long connectTimeout = 70
			, socketTimeout = 10000l
			, socketFailTimeout = 0l
			,queueShortTimeout = 600l
			,queueTimeout = 3000l
            , robinTime = 500
            ,maxResponseTime = 0l
            , shortRetryTime = 0l
            ;

	final String name ;
	
	final String[] servers;
	
	public TcpServerConfig( String name,String... servers) {
		super();
		if (servers.length == 0)
            throw new IllegalArgumentException("config is NULL");
		this.servers = servers;
		this.name = name;
	}
	
}
