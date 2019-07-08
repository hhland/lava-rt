/*
 * Created on 2006-11-24
 *
 */
package lava.rt.aio.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

import lava.rt.aio.ConnectionPool;
import lava.rt.aio.RequestFactory;
import lava.rt.logging.Log;


/**
 * ���ӳ�
 * 
 * 1. �ṩ��������. ����������,���ⲻ�����κεĴ�����Ϣ.
 * 2. ����������. ������������ַ.
 * 3. ֧�ֶ��߳�
 * 
 * @author LiuMingzhu (mingzhuliu@sohu-inc.com)
 *
 */
public abstract class UdpGenericConnectionPool extends ConnectionPool {

	/// random
	protected static final Random random = new Random();
	
	/// ���������״̬��Ϣ
	protected UdpServerStatus[] status ;
	///	socket����ʧ��ʱ�����Զ�ѡ��һ��������ӣ����Ʒ��inplaceConnectionLife��query���Զ��Ͽ�
	protected int inplaceConnectionLife = 500;

	
	
	protected UdpReceiver recver;
	protected UdpSender sender;
	
	
	
	// ���Ӷ����factory
	protected UdpClientFactory factory;
	// �������factory
	protected RequestFactory requestFactory;

	protected ServerConfig serverConfig;
	
	/**
	 * �������ӳ�ʵ��
	 * @param factory ���Ӷ����factory���ض������ӳ�Ҫ�Լ�ʵ�����Ӷ���
	 * @param name ���ӳص����֣��������ⶨ�塣��Ϊnull�ᰴ��"Pool"������
	 */
	protected UdpGenericConnectionPool(UdpClientFactory factory, String name)
	{
		this(factory, name, null);
	}
	/**
	 * ������ʵ��
	 * @param factory ���Ӷ����factory���ض������ӳ�Ҫ�Լ�ʵ�����Ӷ���
	 * @param name ���ӳص����֣��������ⶨ�塣��Ϊnull�ᰴ��"Pool"������
	 * @param reqestFactory ���Ͷ���(Request)��factory
	 */
	protected UdpGenericConnectionPool(UdpClientFactory factory, String name, RequestFactory reqestFactory)
	{
		this.factory = factory;
		if( name != null){
			this.serverConfig.name = name;
		}
		this.requestFactory = reqestFactory;
	}

	public void init() throws Exception{
		
		ArrayList servers = new ArrayList();
		
		if ( this.serverConfig.servers == null ) throw new IllegalArgumentException("config is NULL");
		
		String[] list = pat.split( this.serverConfig.servers );
	
		for (int i = 0 ; i < list.length ; i++ ) {
			UdpServerStatus ss = new UdpServerStatus( list[i], this );
			servers.add( servers.size() , ss);
		}

		UdpServerStatus[] serverStatus = (UdpServerStatus[])servers.toArray( new UdpServerStatus[servers.size()] );

		selector = Selector.open();
		
		this.status = serverStatus;
		
		recver = new UdpReceiver(this);
		sender = new UdpSender(this);
		
		recver.startThread();
		sender.startThread();
	}

	/**
	 * ��ü�¼��ʵ��
	 * @return
	 */
	protected abstract Log getLogger();

	public int sendRequest( UdpRequest request ){
		
		if( request == null ){			
			return -1;
		}
		
		if( ! request.isValid() ){
			request.illegalRequest();
			request.setConnectionErrorStatus(-2);
			return -2;
		}
		
		int serverCount = this.getServerIdCount();
		int ret = request.getServerId( serverCount );
		
		// ��������״̬
		if( ! isServerAvaliable( ret )){
//			System.out.println("server is not avaliable");
			int avaliableServerCount = 0;
			for(int i=0; i< getServerIdCount() ; i++){
				if( isServerAvaliable( i ) ){
					avaliableServerCount ++;
				}
			}
			if( avaliableServerCount <= 0 ){
				request.serverDown();
				request.setConnectionErrorStatus(-1);
				return -1;
			}
			// ���Դ���.
			int inc = ( request.getServerId( avaliableServerCount) ) + 1;

			int finalIndex = ret ;

			int i=0;
			do{
				int j=0;
				boolean find = false;
				do {
					finalIndex = ( finalIndex +1 ) % serverCount;
					if( isServerAvaliable( finalIndex ) ){
						find = true;
						break;
					}
					j++;
				}while( j < serverCount );
				
				if( !find ){
					request.serverDown();
					request.setConnectionErrorStatus(-1);
					return -1;
				}

				i++;
			}while( i<inc);

			ret = finalIndex;
		}
		
		return sendRequestById(ret, request);
	}

	/**
	 * ��ָ����Server��������
	 * ���������ӳصĺܶ���Բ���
	 * ���ã������ڶ����ӳ��ڲ�������Ϥ�Ŀ�����
	 * @param serverId
	 * @param request
	 * @return
	 */
	public int sendRequestById( int serverId, UdpRequest request ){
		// ���serverId�ĺϷ���
		assert( serverId >= 0 && serverId < this.getServerIdCount() );
		if( serverId<0 || serverId>=this.getServerIdCount() ){
			request.illegalRequest();
			request.setConnectionErrorStatus(-1);
			return -1;
		}
		
		request.setServerId(serverId);
		UdpServerStatus ss = getStatus(serverId);
		
		if( ss == null ){
			request.serverDown();
			request.setConnectionErrorStatus(-2);
			return -2;
		}
		
		request.setServer(ss);
		request.setServerInfo( ss.getServerInfo() );
		request.queueSend();
		sender.senderSendRequest(request);

		return 0;
		
	}

	/**
	 * @return
	 */
	public int getServerIdBits() {
		return 0;
	}

	/**
	 * @return
	 */
	public int getServerIdMask() {
		return 0;
	}

	/**
	 * @param i
	 * @return	��į������
	 */
	public InetSocketAddress getServer(int i) {
		return status[i].getAddr();
	}

	/**
	 * @return Returns the inplaceConnectionLife.
	 */
	public int getInplaceConnectionLife() {
		return inplaceConnectionLife;
	}
	
	/**
	 * @param inplaceConnectionLife The inplaceConnectionLife to set.
	 */
	public void setInplaceConnectionLife(int inplaceConnectionLife) {
		this.inplaceConnectionLife = inplaceConnectionLife;
	}
	
	private static Pattern pat = Pattern.compile("\\s+");
	

	public UdpServerStatus[] getAllStatus() {
		return status;
	}
	/**
	 * ����ָ����ŵķ�������״̬����.
	 * @param i
	 * @return ���ָ����ŵķ�����������,�򷵻�null
	 */
	public UdpServerStatus getStatus(int i ){
		if( status != null 
				&& i>=0 
				&& i<status.length ){
			return status[i];
		} else {
			return null;
		}
	}

	public boolean isServerAvaliable(int i){
		long now = System.currentTimeMillis();
		
		UdpServerStatus ss = null;
		if( status !=null && i>=0 && i< status.length){
			ss = status[i];
		}
		if( ss == null ){
			return false;
		}
		boolean ret = ( ss.recentErrorNumber <= this.serverConfig.getMaxErrorsBeforeSleep()
				|| (now - ss.downtime ) >= this.serverConfig.getSleepMillisecondsAfterTimeOutError() );
		if( !ret ){
			Log logger = getLogger();
			
				logger.info("server is not avaliable:" + ss.getServerInfo() );
		}
		return ret;
	}
	
	/**
	 * �������i��Ӧ�ķ����������ӳ��ж�Ӧ�ļ�ֵ.
	 * �����Ӧ�ķ������Ƿ�(������),�򷵻�null;
	 * @param i
	 * @return
	 */
	public Object getServerKey( int i ){
		if( status !=null 
				&& i >= 0
				&& i < status.length
				&& status[i] !=null
				&& status[i].key != null
			) {
			return status[i].key;
		} else {
			return null;
		}
	}

	/**
	 * ���й�����, server���ܻ�down��, ��Ҫ��̬��������������ֵ.
	 * @return
	 */
	public int getServerIdCount(){
		if( status == null ){
			return 0;
		}else {
			return status.length;
		}
	}
	public InetSocketAddress getSocketAddress( int i ){
		if( status == null 
				|| i<0
				|| i>=status.length
				|| status[i] == null
		  ){
			return null;
		} else {
			return status[i].getAddr();
		}
	}
	public void finalize(){
		destroy();
	}
	
	/**
	 * �������ӳض���
	 */
	public void destroy(){
		sender.stopThread();
		sender = null;
		recver.stopThread();
		recver = null;
		UdpServerStatus[] temp = status;
		status = null;
		if( temp != null ){
			for(int i=0;i<temp.length; i++){
				UdpServerStatus ss = temp[i];
				if( ss == null ) continue;
				ss.destroy();
			}
		}
		try{
			this.selector.close();
		}catch( IOException e){
			// dummy
		}
	}
	public String status(){
		StringBuffer sb = new StringBuffer();
		sb.append( "\nPool Status: ");
		sb.append( this.serverConfig.getName() );
		sb.append( '\n' );
		
		for( int i=0; i< this.status.length; i++){
			status[i].status( sb );
		}
		
		
		getLogger().info( sb.toString() );
		
		return sb.toString();
	}

	/**
	 * @return the requestFactory
	 */
	public RequestFactory getRequestFactory() {
		return requestFactory;
	}

	/**
	 * @param requestFactory the requestFactory to set
	 */
	public void setRequestFactory(RequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	
	
	
	public class ServerConfig {


		/// �������ٴδ����,����sleep����
		protected int maxErrorsBeforeSleep = 4;
		///	��������󣬶೤ʱ���ڲ������³���
		protected int sleepMillisecondsAfterTimeOutError = 6000;
		protected int maxConnectionsPerServer = 1;
		protected long connectTimeout = 70;
		protected long socketTimeout = 100l;
		protected long queueTimeout = 1000l;
		protected long robinTime = 500;
		protected int maxQueueSize = 10000;
		protected long minProbeTime = 1000l;
		String servers;
		String name = "Pool";

		public String getServers() {
			return servers;
		}

		public void setServers(String servers) {
			this.servers = servers;
		}

		public int getMaxErrorsBeforeSleep() {
			return maxErrorsBeforeSleep;
		}

		public void setMaxErrorsBeforeSleep(int maxErrorsBeforeSleep) {
			this.maxErrorsBeforeSleep = maxErrorsBeforeSleep;
		}

		public int getSleepMillisecondsAfterTimeOutError() {
			return sleepMillisecondsAfterTimeOutError;
		}

		public synchronized void setSleepMillisecondsAfterTimeOutError(
				int sleepMillisecondsAfterTimeOutError) {
			this.sleepMillisecondsAfterTimeOutError = sleepMillisecondsAfterTimeOutError;
		}

		public int getMaxConnectionsPerServer() {
			return maxConnectionsPerServer;
		}

		public void setMaxConnectionsPerServer(int maxConnectionsPerServer) {
			this.maxConnectionsPerServer = maxConnectionsPerServer;
		}

		public long getConnectTimeout() {
			return connectTimeout;
		}

		public void setConnectTimeout(long connectTimeout) {
			this.connectTimeout = connectTimeout;
		}

		public long getSocketTimeout() {
			return socketTimeout;
		}

		public void setSocketTimeout(long socketTimeout) {
			this.socketTimeout = socketTimeout;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getQueueTimeout() {
			return queueTimeout;
		}

		public void setQueueTimeout(long queueTimeout) {
			this.queueTimeout = queueTimeout;
		}

		public long getRobinTime() {
			return robinTime;
		}

		public void setRobinTime(long robinTime) {
			this.robinTime = robinTime;
		}

		/**
		 * @return the maxQueueSize
		 */
		public int getMaxQueueSize() {
			return maxQueueSize;
		}

		/**
		 * @param maxQueueSize the maxQueueSize to set
		 */
		public void setMaxQueueSize(int maxQueueSize) {
			this.maxQueueSize = maxQueueSize;
		}

		/**
		 * @return the minProbeTime
		 */
		public long getMinProbeTime() {
			return minProbeTime;
		}

		/**
		 * @param minProbeTime the minProbeTime to set
		 */
		public void setMinProbeTime(long minProbeTime) {
			this.minProbeTime = minProbeTime;
		}

		}
	
	
	
}
