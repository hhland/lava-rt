package lava.rt.aio.tcp;

public class TcpServerConfig   {

	
	/// �������ٴδ����,����sleep����
	protected  int maxErrorsBeforeSleep = 4
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

	  String name ;
	
	public  String[] servers;
	
	public TcpServerConfig( String name,String... servers) {
		super();
		if (servers.length == 0)
            throw new IllegalArgumentException("config is NULL");
		this.servers = servers;
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxErrorsBeforeSleep()
	 */
	  
	public int getMaxErrorsBeforeSleep() {
		return maxErrorsBeforeSleep;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxErrorsBeforeSleep(int)
	 */
	  
	public void setMaxErrorsBeforeSleep(int maxErrorsBeforeSleep) {
		this.maxErrorsBeforeSleep = maxErrorsBeforeSleep;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getSleepMillisecondsAfterTimeOutError()
	 */
	  
	public int getSleepMillisecondsAfterTimeOutError() {
		return sleepMillisecondsAfterTimeOutError;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setSleepMillisecondsAfterTimeOutError(int)
	 */
	  
	public void setSleepMillisecondsAfterTimeOutError(int sleepMillisecondsAfterTimeOutError) {
		this.sleepMillisecondsAfterTimeOutError = sleepMillisecondsAfterTimeOutError;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxConnectionsPerServer()
	 */
	  
	public int getMaxConnectionsPerServer() {
		return maxConnectionsPerServer;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxConnectionsPerServer(int)
	 */
	  
	public void setMaxConnectionsPerServer(int maxConnectionsPerServer) {
		this.maxConnectionsPerServer = maxConnectionsPerServer;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxClonedRequest()
	 */
	  
	public int getMaxClonedRequest() {
		return maxClonedRequest;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxClonedRequest(int)
	 */
	  
	public void setMaxClonedRequest(int maxClonedRequest) {
		this.maxClonedRequest = maxClonedRequest;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxQueueSize()
	 */
	  
	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxQueueSize(int)
	 */
	  
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxResponseRadio()
	 */
	  
	public int getMaxResponseRadio() {
		return maxResponseRadio;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxResponseRadio(int)
	 */
	  
	public void setMaxResponseRadio(int maxResponseRadio) {
		this.maxResponseRadio = maxResponseRadio;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getConnectTimeout()
	 */
	  
	public long getConnectTimeout() {
		return connectTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setConnectTimeout(long)
	 */
	  
	public void setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getSocketTimeout()
	 */
	  
	public long getSocketTimeout() {
		return socketTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setSocketTimeout(long)
	 */
	  
	public void setSocketTimeout(long socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getSocketFailTimeout()
	 */
	  
	public long getSocketFailTimeout() {
		return socketFailTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setSocketFailTimeout(long)
	 */
	  
	public void setSocketFailTimeout(long socketFailTimeout) {
		this.socketFailTimeout = socketFailTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getQueueShortTimeout()
	 */
	  
	public long getQueueShortTimeout() {
		return queueShortTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setQueueShortTimeout(long)
	 */
	  
	public void setQueueShortTimeout(long queueShortTimeout) {
		this.queueShortTimeout = queueShortTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getQueueTimeout()
	 */
	  
	public long getQueueTimeout() {
		return queueTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setQueueTimeout(long)
	 */
	  
	public void setQueueTimeout(long queueTimeout) {
		this.queueTimeout = queueTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getRobinTime()
	 */
	  
	public long getRobinTime() {
		return robinTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setRobinTime(long)
	 */
	  
	public void setRobinTime(long robinTime) {
		this.robinTime = robinTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxResponseTime()
	 */
	  
	public long getMaxResponseTime() {
		return maxResponseTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxResponseTime(long)
	 */
	  
	public void setMaxResponseTime(long maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getShortRetryTime()
	 */
	  
	public long getShortRetryTime() {
		return shortRetryTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setShortRetryTime(long)
	 */
	  
	public void setShortRetryTime(long shortRetryTime) {
		this.shortRetryTime = shortRetryTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getName()
	 */
	  
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setName(java.lang.String)
	 */
	  
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getServers()
	 */
	  
	public String[] getServers() {
		return servers;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setServers(java.lang.String[])
	 */
	  
	public void setServers(String[] servers) {
		this.servers = servers;
	}
	
	
	
	
	
}
