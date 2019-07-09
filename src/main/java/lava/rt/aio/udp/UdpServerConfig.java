package lava.rt.aio.udp;

public class UdpServerConfig {


	
	public final int maxErrorsBeforeSleep = 4
			,sleepMillisecondsAfterTimeOutError = 6000
	        , maxConnectionsPerServer = 1
	        ,maxQueueSize = 10000
	        ;
	
	public final long connectTimeout = 70
	  , socketTimeout = 100l
	  , queueTimeout = 1000l
	  , robinTime = 500
	  , minProbeTime = 1000l
	  ;
	
	public final String name;
	
	public final String[]  servers;
	

	public UdpServerConfig(String name,String...servers ) {
		super();
		if (servers.length == 0)
            throw new IllegalArgumentException("config is NULL");
		this.servers = servers;
		this.name = name;
	}

	

}
