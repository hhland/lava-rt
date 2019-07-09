package lava.rt.aio.tcp;

public class TcpServerConfig implements TcpServerConfigMBean  {

	
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
	@Override
	public int getMaxErrorsBeforeSleep() {
		return maxErrorsBeforeSleep;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxErrorsBeforeSleep(int)
	 */
	@Override
	public void setMaxErrorsBeforeSleep(int maxErrorsBeforeSleep) {
		this.maxErrorsBeforeSleep = maxErrorsBeforeSleep;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getSleepMillisecondsAfterTimeOutError()
	 */
	@Override
	public int getSleepMillisecondsAfterTimeOutError() {
		return sleepMillisecondsAfterTimeOutError;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setSleepMillisecondsAfterTimeOutError(int)
	 */
	@Override
	public void setSleepMillisecondsAfterTimeOutError(int sleepMillisecondsAfterTimeOutError) {
		this.sleepMillisecondsAfterTimeOutError = sleepMillisecondsAfterTimeOutError;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxConnectionsPerServer()
	 */
	@Override
	public int getMaxConnectionsPerServer() {
		return maxConnectionsPerServer;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxConnectionsPerServer(int)
	 */
	@Override
	public void setMaxConnectionsPerServer(int maxConnectionsPerServer) {
		this.maxConnectionsPerServer = maxConnectionsPerServer;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxClonedRequest()
	 */
	@Override
	public int getMaxClonedRequest() {
		return maxClonedRequest;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxClonedRequest(int)
	 */
	@Override
	public void setMaxClonedRequest(int maxClonedRequest) {
		this.maxClonedRequest = maxClonedRequest;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxQueueSize()
	 */
	@Override
	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxQueueSize(int)
	 */
	@Override
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxResponseRadio()
	 */
	@Override
	public int getMaxResponseRadio() {
		return maxResponseRadio;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxResponseRadio(int)
	 */
	@Override
	public void setMaxResponseRadio(int maxResponseRadio) {
		this.maxResponseRadio = maxResponseRadio;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getConnectTimeout()
	 */
	@Override
	public long getConnectTimeout() {
		return connectTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setConnectTimeout(long)
	 */
	@Override
	public void setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getSocketTimeout()
	 */
	@Override
	public long getSocketTimeout() {
		return socketTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setSocketTimeout(long)
	 */
	@Override
	public void setSocketTimeout(long socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getSocketFailTimeout()
	 */
	@Override
	public long getSocketFailTimeout() {
		return socketFailTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setSocketFailTimeout(long)
	 */
	@Override
	public void setSocketFailTimeout(long socketFailTimeout) {
		this.socketFailTimeout = socketFailTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getQueueShortTimeout()
	 */
	@Override
	public long getQueueShortTimeout() {
		return queueShortTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setQueueShortTimeout(long)
	 */
	@Override
	public void setQueueShortTimeout(long queueShortTimeout) {
		this.queueShortTimeout = queueShortTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getQueueTimeout()
	 */
	@Override
	public long getQueueTimeout() {
		return queueTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setQueueTimeout(long)
	 */
	@Override
	public void setQueueTimeout(long queueTimeout) {
		this.queueTimeout = queueTimeout;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getRobinTime()
	 */
	@Override
	public long getRobinTime() {
		return robinTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setRobinTime(long)
	 */
	@Override
	public void setRobinTime(long robinTime) {
		this.robinTime = robinTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getMaxResponseTime()
	 */
	@Override
	public long getMaxResponseTime() {
		return maxResponseTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setMaxResponseTime(long)
	 */
	@Override
	public void setMaxResponseTime(long maxResponseTime) {
		this.maxResponseTime = maxResponseTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getShortRetryTime()
	 */
	@Override
	public long getShortRetryTime() {
		return shortRetryTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setShortRetryTime(long)
	 */
	@Override
	public void setShortRetryTime(long shortRetryTime) {
		this.shortRetryTime = shortRetryTime;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#getServers()
	 */
	@Override
	public String[] getServers() {
		return servers;
	}

	/* (non-Javadoc)
	 * @see lava.rt.aio.tcp.TcpServerConfigMBean#setServers(java.lang.String[])
	 */
	@Override
	public void setServers(String[] servers) {
		this.servers = servers;
	}
	
	
	
	
	
}
