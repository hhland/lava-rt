package lava.rt.aio.tcp;

public interface TcpServerConfigMBean {

	int getMaxErrorsBeforeSleep();

	void setMaxErrorsBeforeSleep(int maxErrorsBeforeSleep);

	int getSleepMillisecondsAfterTimeOutError();

	void setSleepMillisecondsAfterTimeOutError(int sleepMillisecondsAfterTimeOutError);

	int getMaxConnectionsPerServer();

	void setMaxConnectionsPerServer(int maxConnectionsPerServer);

	int getMaxClonedRequest();

	void setMaxClonedRequest(int maxClonedRequest);

	int getMaxQueueSize();

	void setMaxQueueSize(int maxQueueSize);

	int getMaxResponseRadio();

	void setMaxResponseRadio(int maxResponseRadio);

	long getConnectTimeout();

	void setConnectTimeout(long connectTimeout);

	long getSocketTimeout();

	void setSocketTimeout(long socketTimeout);

	long getSocketFailTimeout();

	void setSocketFailTimeout(long socketFailTimeout);

	long getQueueShortTimeout();

	void setQueueShortTimeout(long queueShortTimeout);

	long getQueueTimeout();

	void setQueueTimeout(long queueTimeout);

	long getRobinTime();

	void setRobinTime(long robinTime);

	long getMaxResponseTime();

	void setMaxResponseTime(long maxResponseTime);

	long getShortRetryTime();

	void setShortRetryTime(long shortRetryTime);

	String getName();

	void setName(String name);

	String[] getServers();

	void setServers(String[] servers);

}