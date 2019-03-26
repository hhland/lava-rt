package lava.rt.pool.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;


public class AsyncServerStatus {
	
	private static Log logger = LogFactory.getLog(AsyncServerStatus.class);

	Object key;
	
	/**
	 * Runtime Status
	 */
	volatile int recentErrorNumber;
	volatile int retryCount = 0;
	volatile Long downtime = 0l;
	
	volatile boolean longRequestDead = false; 
	
	/**
	 * Configuration
	 */
	InetSocketAddress addr;
	String serverInfo;
	
	/*
	 * server swithcer
	 * 
	 */
	boolean swithcer = true;
	
	/*
	 * should clone flag
	 */
	protected boolean shouldCloneFlag = true;
	
	protected LinkedList waitQueue = new LinkedList();
	protected LinkedList freeChannelList;
	ArrayList allClients;
	
	
	AsyncGenericConnectionPool pool;
	
	/**
	 * ���캯��
	 *
	 */
	public AsyncServerStatus() {
	}

	/**
	 * called by Receiver to check if any client has reached a socket timeout
	 *
	 */
	public void checkTimeout (){
		long now = System.currentTimeMillis();
		for( int i=0; i< allClients.size();i++){
			AsyncGenericQueryClient c = (AsyncGenericQueryClient)allClients.get(i);
			
			do{
				if( !c.isValid() ) break;
				
				// check if Clinet is free.
				if( ! c.using ) break;
				
				
				// check time value.
				boolean isSocketTimeout = true;
				if( ( !c.requestSent
						|| c.getTime_request() == 0
						|| 	now - c.getTime_request() <= pool.getServerConfig().getSocketTimeout() )
						){
					isSocketTimeout = false;
				}
				boolean isConnectTimeout = true;
				if( ( c.isConnected() 
						|| c.getTime_connect() == 0
						|| now - c.getTime_connect() <= pool.getServerConfig().getConnectTimeout() ) )
				{
//					System.out.println("Socket or IO not timeOut"+c.requestSent);
					isConnectTimeout = false;
				}
				
				if (!isSocketTimeout && !isConnectTimeout){
					break;
				}
				
				boolean isSocketFailTimeout = true;
				if( ( !c.requestSent
						|| c.getTime_request() == 0
						|| 	now - c.getTime_request() <= pool.getServerConfig().getSocketFailTimeout() )
						){
					isSocketFailTimeout = false;
				}
				
				
				
				AsyncRequest request = c.getRequest();			
				if (isConnectTimeout || isSocketFailTimeout)
				{
					//�����һ������ʧ���˵�����
					if( logger.isTraceEnabled() ){
						logger.trace("Socket TimeOut!!!"+c.requestSent+" "+c.getTime_request()
								+ " "+(now - c.getTime_request())+" "+pool.getServerConfig().getSocketTimeout() 
								+ "\nIO TimeOut!!!"+c.isConnected()+" "+c.getTime_connect()
								+ " "+(now - c.getTime_connect())+" "+ pool.getServerConfig().getConnectTimeout());
					}
					if( request != null ){
						if (isSocketFailTimeout)
							request.socketTimeout();
						else{
							if (isConnectTimeout)
								request.connectTimeout();
						}
					}
					c.serverStatus.socketTimeout();
					
					c.close();
					try{
						c.reset();
					}catch( Exception e){
						// ���Լ���
					}
					freeClient( c );
				}else{
					//�̳�ʱ����
					if (request != null && request.clonableRequest && request.clonedTo == null && request.clonedFrom == null){
						//debug bart
						if (shouldClone()){
							System.out.println("[pool "+request.ruid+"]Short timeout is trigged for "+request.getServerInfo());
							//��request����clone��������δ��clone
							AsyncRequest request_cloned = request.clone();
							request_cloned.connectType = AsyncRequest.SHADOW_NORMAL_REQUEST;
							pool.sendRequest(request_cloned);
						}
					}
				}
			}while( false );
		}
	}

	/**
	 * ��������.
	 * ���û��߳�ͨ��AsyncConnectionPool.sendRequest����
	 * @param req
	 * @return
	 *  -3 No Resource Error
	 *  -2 server is Down
	 *  -1 request is Invalid
	 *   0 sent Successfully
	 *   1 Server Busy
	 */
	int serverSendRequest(AsyncRequest request) {
		// ����������Ϸ���, �ͼ���ȴ�����.
		boolean needQueue = false;
		
		synchronized( waitQueue ){
			if( this.waitQueue.size() > 0 ){
				needQueue = true;
			}
		}
		if( needQueue ){
			
			queueRequest( request );
//			if( logger.isTraceEnabled() ){
//				logger.trace("put a request in the queue");
//			}
			return 1;
		} else {
			int sts = innerSendRequest( request );
			
			if( sts > 0 ){
//				if( logger.isTraceEnabled() ){
//					logger.trace("No avaliable channal put request in the queue");
//				}
				queueRequest(request );
			}
			return sts;
		}

	}
	/**
	 * ��������
	 * �� Sender �̵߳���
	 * @return
	 */
	int innerSendRequest() {
		// ����������Ϸ���, �ͼ���ȴ�����.
		int sts;
		AsyncRequest request = firstUserRequest();
		
		if( request == null ) return 1;
		
		sts = innerSendRequest( request );
		switch(sts){
		case 1:
			// ������æ,
			// û�п��е�����
			// do nothing
			break;
		case 0:
			// ���ͳɹ�,
			// �Ӷ����Ƴ�����.
			request.time_outwaitqueue();
			removeFirstUserRequest();
			break;
		case -1:
			// �Ƿ�����,
			// �Ӷ����Ƴ�����, ��֪ͨ�û��߳�.
			request.time_outwaitqueue();
			removeFirstUserRequest();
			break;
		case -2:
			// ������������
			// �Ӷ����Ƴ�����, ��֪ͨ�û��߳�.
			request.time_outwaitqueue();
			removeFirstUserRequest();
			break;
		case -3:
			// ϵͳ��Դ����
			// �Ӷ����Ƴ�����, ��֪ͨ�û��߳�.
			request.time_outwaitqueue();
			removeFirstUserRequest();
			break;
		case -4:
			// ����ʱ
			// �Ӷ����Ƴ�����, ��֪ͨ�û��߳�.
			request.time_outwaitqueue();
			removeFirstUserRequest();
			break;
		default:
			request.time_outwaitqueue();
			removeFirstUserRequest();
		}
		return sts;

	}
	/**
	 * ��������.
	 * @param req
	 * @return
	 *  -4 request timeout
	 *  -3 No Resource Error
	 *  -2 server is Down
	 *  -1 request is Invalid
	 *   0 sent Successfully
	 *   1 Server Busy
	 */
	int innerSendRequest(AsyncRequest request) {
		
		if (!isServerAlive() && request.connectType != AsyncRequest.RETRY_REQUEST) {
			if( logger.isTraceEnabled()) {
				logger.trace("server is down, Don't waste Time");
			}
			request.serverDown("[innerSend]������������");
			return -2;
		}
		
		long now = System.currentTimeMillis();
		
		if (now - request.getStartTime() > pool.getServerConfig().getQueueTimeout()) { // �Ŷӳ�ʱ
			if( logger.isTraceEnabled() ){
				logger.trace("WaitQueue timeOut");
			}
			queueTimeout();
			request.waitTimeout();
			return -4;
		}
		
		if (now - request.getStartTime() > pool.getServerConfig().getQueueShortTimeout()) { // �ŶӶ̳�ʱ
			//�ŶӶ̳�ʱ����
				//debug bart
				//�����Ŷӳ�ʱ��ת��
				if (shouldCloneFlag){
					LinkedList sendQueue = new LinkedList();
					synchronized( waitQueue){
						for( int i=0; i<waitQueue.size(); i++){
							AsyncRequest req = (AsyncRequest)waitQueue.get(i);
							if (now - req.getStartTime() > pool.getServerConfig().getQueueShortTimeout()){
								if (req.clonableRequest && req.clonedTo == null && req.clonedFrom == null){
									System.out.println("[pool "+req.ruid+"]Short queue timeout is trigged for "+req.getServerInfo());
									//��request����clone��������δ��clone
									AsyncRequest request_cloned = req.clone();
									request_cloned.connectType = AsyncRequest.SHADOW_QUEUE_REQUEST;
									sendQueue.addLast(request_cloned);
								}
							}
						}
					}
					for( int i=0; i<sendQueue.size(); i++){
						AsyncRequest req = (AsyncRequest)sendQueue.get(i);
						pool.sendRequest(req);
					}
				}
		}

		do{
			AsyncGenericQueryClient sc = removeFirstFreeClient();

			if (sc == null){
				if( logger.isTraceEnabled() ){
					logger.trace("CLIENT RUNOUT!");
				}
				break;
			}

			/**
			 * @XXX ���Ӵ�������
			 */
			boolean isRegistered = sc.isRegistered();
			if( ! isRegistered ){ // NOT regiestered. �̰߳�ȫ

				if ( ! sc.isValid() ) {
					boolean isClientValid = false;
					try {
						sc.connect(addr);
						sc.setTime_connect();
						request.time_connect();
						isClientValid = true;
					} catch (IOException e) {
						if (logger.isWarnEnabled()) {
							logger.warn("Sender: open new SocketChannel ", e);
						}
					}
					if ( !isClientValid ) {
						request.serverDown("[innSend]����ʧ��");
						freeClient(sc);
						noResourceError();
						return -3;
					}
				}
				
				if (!sc.isConnected()) { // �����첽��������
					if( logger.isTraceEnabled() ){
						logger.trace("NOT CONNECTED!");
					}
					sc.setRequest(request);

					sc.requestSent(false);
					request.time_enqueue();
					pool.recver.queueChannel(sc);
					pool.selector.wakeup();
					request.time_enqueue_end();
					return 0;
				}
			}

			sc.setRequest(request);

			// ��������
			if (logger.isTraceEnabled()) {
				logger.trace("TO SEND REQ!");
			}

			request.time_connect_end();

			int status = -1;
			try {
				status = sc.sendRequest();
			} catch (IOException e) {
				sendError();
				if (logger.isWarnEnabled()) {
					logger.warn("IOE", e);
				}
				sc.close();
			} catch (RuntimeException e) {
				if (logger.isErrorEnabled()) {
					logger.error("RTE While sending Request(Non-IOE)", e);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Exception While sending Request(Non-IOE)", e);
				}
			}

			if (status > 0) {

				/**
				 * ���ͳɹ�, ���channel�Ƿ��Ѿ�ע��. ��Ϊǰ��finishConnect�����Ѿ����ӳɹ�,
				 * ������ֱ�ӷ���,Ȼ�����������ע������.
				 */
				if (logger.isTraceEnabled()) {
					logger.trace("SENT SUCCESS!");
				}

				request.requestTime();
				sc.setTime_request();
				sc.requestSent(true);

				if (!isRegistered) {
					if (logger.isTraceEnabled()) {
						logger.trace("NOT REGED!");
					}
					request.time_enqueue();
					pool.recver.queueChannel(sc);
					pool.selector.wakeup();
					request.time_enqueue_end();
				}

				return 0;
			} else {
				/**
				 * ���Ͳ��ɹ� �����ֿ���: socket��request. �����socket������,
				 * ��ôrecver����ȷ����(close). �����request������, ��ô��֪ͨ�û��̼߳���.
				 */
				if (logger.isTraceEnabled()) {
					logger.trace("ILLEGAL REQUEST!");
				}
				if(status == 0) {
					request.illegalRequest();
				}else {
					request.serverDown("[innSnd]����ʧ��");
				}
				try {
					sc.reset();
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.debug("Exception while reset client", e);
					}
					// ignore
				}
				freeClient(sc);
				return -1;
			}

			/**
			 * @XXX ���Ӵ������
			 */
		} while( false );
		
		return 1;
	}
	
	private AsyncGenericQueryClient removeFirstFreeClient(){
		AsyncGenericQueryClient client;
		synchronized( freeChannelList ){
			if( freeChannelList.size() > 0 ){
				if( logger.isTraceEnabled() ){
					logger.trace("Get One Client From " + freeChannelList.size());
				}
				client = (AsyncGenericQueryClient)freeChannelList.removeFirst( );
				client.using = true;
				client.requestSent = false;
			} else {
				if( logger.isTraceEnabled() ){
					logger.trace("no Free Client to get");
				}
				return null;
			}
		}
		return client;
	}
	void freeClient(AsyncGenericQueryClient client) {
		synchronized (freeChannelList) {
			if (client.using) {
				if( logger.isTraceEnabled() ){
					logger.trace("Free a Client");
				}
				client.using = false;
				freeChannelList.addFirst(client);
			}
		}
		/**
		 * @TODO check Sender Thread
		 */
		this.pool.sender.checkSenderThread();
	}

	private AsyncRequest firstUserRequest(){
		synchronized( waitQueue ){
			if( waitQueue.isEmpty() ){
				return null;
			} else {
				return (AsyncRequest)this.waitQueue.element();
			}
		}
	}

	private AsyncRequest removeFirstUserRequest(){
		AsyncRequest client;
		synchronized( waitQueue ){
			client = (AsyncRequest)waitQueue.removeFirst( );
		}
		if( logger.isTraceEnabled() ){
			logger.trace("Remove a request from the queue");
		}
		return client;
	}

	public boolean isServerAvaliable(){
		return (swithcer && isServerAlive() && !longRequestDead);
	}
	
	public boolean isServerAlive(){
		long now = System.currentTimeMillis();
		int waitQueueSize = 0;
		synchronized( waitQueue ){
			waitQueueSize = waitQueue.size();
		}
		return (( recentErrorNumber <= pool.getServerConfig().maxErrorsBeforeSleep
				|| (now - downtime ) >= pool.getServerConfig().sleepMillisecondsAfterTimeOutError 
			) && waitQueueSize <= pool.getServerConfig().maxQueueSize);
	}
	
	public boolean isServerShouldRerty(){
		if (!swithcer)
			return false;
		long now = System.currentTimeMillis();
		if (( retryCount <= 0  && recentErrorNumber > pool.getServerConfig().maxErrorsBeforeSleep 
				&& (now - downtime ) >= pool.getServerConfig().getShortRetryTime() ))
			return true;
		if (recentErrorNumber <= pool.getServerConfig().maxErrorsBeforeSleep && longRequestDead)
			return true;
		return false;
	}

	public String getServerInfo() {
		if( serverInfo == null 
				&& this.key!=null 
				&& this.addr != null ){
			serverInfo = this.key.toString() + '@'+this.addr.getAddress().getHostAddress()+':' + this.addr.getPort();
		}
		return serverInfo;
	}

	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

//	public ServerStatus(){}
	
	public AsyncServerStatus(String line, AsyncGenericConnectionPool pool ) throws IllegalArgumentException{
		if( line == null ) throw new IllegalArgumentException("ServerStatus Null Line Parameter");
		
		line = line.trim();
		
		int at = line.indexOf('@');
		if( at < 0 ) throw new IllegalArgumentException( "invalid line:" + line );
		
		int sc = line.indexOf(':', at + 1);
		if( sc < 0 ) throw new IllegalArgumentException( "invalid line:" + line );
		
		int port;
		try{	
			port = Integer.parseInt( line.substring(sc + 1) );
		}catch( NumberFormatException e){
			throw new IllegalArgumentException( e );
		}
		
		InetSocketAddress addr;
		try{
			addr = new InetSocketAddress( 
					InetAddress.getByName( line.substring( at+1, sc )),
					port );
		}catch( UnknownHostException e){
			throw new IllegalArgumentException( "ServerStatus:invalid host:" + line , e );
		}catch( SecurityException e ){
			throw new IllegalArgumentException( "ServerStatus:invalid line:" + line , e );
		}catch( IllegalArgumentException e){
			throw new IllegalArgumentException( "ServerStatus:invalid Server:" + line , e );
		}
		
		
		this.pool = pool;
		int count = pool.getServerConfig().getMaxConnectionsPerServer();
		ArrayList al = new ArrayList( count );
		LinkedList deactiveChannelSet = new LinkedList();
		for(int i=0;i<count;i++){
			
			AsyncGenericQueryClient ace = pool.factory.newInstance();
			ace.serverStatus = this;
//			SocketChannel socketChannel =null;
//			ace.channel = socketChannel;
			al.add( ace );
			deactiveChannelSet.add( ace );
		}
		
		this.freeChannelList = deactiveChannelSet;
		this.allClients = al;

		
		this.key = line.substring(0, at);
		this.addr = addr;
		this.serverInfo = line;
	}
	
	public long getDowntime() {
		return downtime;
	}

	public void setDowntime(long downtime) {
		this.downtime = downtime;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public int getRecentErrorNumber() {
		return recentErrorNumber;
	}

	public void setRecentErrorNumber(int recentErrorNumber) {
		this.recentErrorNumber = recentErrorNumber;
	}

	public synchronized void connectTimeout(){
		if( logger.isTraceEnabled() ){
			logger.trace("errNumber is " + recentErrorNumber );
		}
		this.recentErrorNumber ++;
		System.out.println("[pool connectError]"+recentErrorNumber+"\t"+serverInfo);
		this.downtime = System.currentTimeMillis();
	}
	public synchronized void socketTimeout() {
		if( logger.isTraceEnabled() ){
			logger.trace("sockettimeout "+ recentErrorNumber);
		}
		this.recentErrorNumber ++;
		System.out.println("[pool socketError]"+recentErrorNumber+"\t"+serverInfo);
		this.downtime = System.currentTimeMillis();
	}
	public synchronized void sendError() {
		this.recentErrorNumber ++;
		System.out.println("[pool sendError]"+recentErrorNumber+"\t"+serverInfo);
		this.downtime = System.currentTimeMillis();
	}
	public synchronized void noResourceError() {
		this.recentErrorNumber ++;
		System.out.println("[pool noresError]"+recentErrorNumber+"\t"+serverInfo);
		this.downtime = System.currentTimeMillis();
	}
	public synchronized void queueTimeout() {
		this.recentErrorNumber ++;
		System.out.println("[pool queueError]"+recentErrorNumber+"\t"+serverInfo);
		this.downtime = System.currentTimeMillis();
	}

	public synchronized void success() {
		this.recentErrorNumber >>= 1;
	}

	public InetSocketAddress getAddr() {
		return addr;
	}

	public void setAddr(InetSocketAddress addr) {
		this.addr = addr;
	}

	protected final int queueRequest(AsyncRequest request){
		request.setServerInfo( this.getServerInfo() );
		synchronized(waitQueue){
			waitQueue.addLast( request );
		}
		return 0;
	}
	public CharSequence queueStatus(StringBuffer sb){
		synchronized( waitQueue){
			sb.append("\nServerStatus:");
			sb.append( this.getKey() );
			sb.append( ", addr: ");
			sb.append( this.addr );
			sb.append( '\n' );

			for( int i=0; i<waitQueue.size(); i++){
				AsyncRequest req = (AsyncRequest)waitQueue.get(i);
				sb.append( req.dumpTimeStatus() );
				sb.append('\n');
			}
		}
		return sb;
	}
	public String status(StringBuffer sb){
		sb.append( "\nServerStatus: key:");
		sb.append( this.getKey() );
		sb.append( ", addr: ");
		sb.append( this.addr );
		sb.append( '\n' );
		sb.append("\tsend_queue: ");
		sb.append( this.waitQueue.size() );
		sb.append( ", swithcer:");
		sb.append( this.swithcer );
		sb.append( ", ava:");
		sb.append( this.isServerAlive() );
		sb.append( ", long_dead:");
		sb.append( this.longRequestDead );
		sb.append( ", free:");
		sb.append( this.freeChannelList.size() );
		sb.append( "\n\tcloned:");
		sb.append( this.totalClone );
		sb.append( ", avg_time:");
		sb.append( this.getServerAvgTime(1) );
		sb.append( ", error_count:");
		sb.append( this.recentErrorNumber );
		sb.append( ", total_req:");
		sb.append( this.marker );
		sb.append( '\n' );
		
		for( int i=0; i< this.allClients.size(); i++){
			sb.append( '\t' );
			AsyncGenericQueryClient ace = (AsyncGenericQueryClient)this.allClients.get(i);
			sb.append( ace.getStatus() );
			sb.append('\n');
		}
		
		if( logger.isInfoEnabled() ){
			logger.info( sb.toString() );
		}
		return sb.toString();
	}
	
	public String statusLog(StringBuffer sb){
		sb.append( "ServerStatus: key:");
		sb.append( this.getKey() );
		sb.append( ", addr: ");
		sb.append( this.addr );
		sb.append(", send_queue: ");
		sb.append( this.waitQueue.size() );
		sb.append( ", swithcer:");
		sb.append( this.swithcer );
		sb.append( ", ava:");
		sb.append( this.isServerAlive() );
		sb.append( ", long_dead:");
		sb.append( this.longRequestDead );
		sb.append( ", free:");
		sb.append( this.freeChannelList.size() );
		sb.append( ", cloned:");
		sb.append( this.totalClone );
		sb.append( ", avg_time:");
		sb.append( this.getServerAvgTime(1) );
		sb.append( ", error_count:");
		sb.append( this.recentErrorNumber );
		sb.append( ", total_req:");
		sb.append( this.marker );
		sb.append( '\t' );
		
		for( int i=0; i< this.allClients.size(); i++){
			AsyncGenericQueryClient ace = (AsyncGenericQueryClient)this.allClients.get(i);
			sb.append( ace.getStatus() );
			sb.append( '.' );
		}
		sb.append( "\t\t\t" );
		
		return sb.toString();
	}
	public void finalize(){
		destroy();
	}
	public void destroy(){
		ArrayList allClients = this.allClients;
		for( int i=0; allClients != null && i< allClients.size();i++){
			AsyncGenericQueryClient c = (AsyncGenericQueryClient)allClients.get(i);
			c.close();
		}
	}
	
	protected final static int MARKER_ARRAY_LENGTH = 10;
	protected boolean cloneMarker[] = new boolean[MARKER_ARRAY_LENGTH];
	protected long timeMarker[] = new long[MARKER_ARRAY_LENGTH];
	protected long marker = 0;
	protected long totalTime = 0;
	protected int totalClone = 0;
	protected Object markerLocker = new Object();
	
	protected void mark(AsyncRequest request){
		synchronized(markerLocker){
			while(true){
				//��������
				if (request.connectType == AsyncRequest.RETRY_REQUEST){
					retryCount--;
				}
				//ʧ�ܵ����󲻽���ͳ��?
				//if (request.status < 0){
				//	break;
				//}
				
				//���MARKER_ARRAY_LENGTH�������б�clone��ȥ�ز�Ĵ���
				boolean isClone = (request.clonedTo != null && request.clonedTo.connectType == AsyncRequest.SHADOW_NORMAL_REQUEST);
				int marker_perarray = (int)(marker%MARKER_ARRAY_LENGTH);
				if (isClone)
					totalClone--;
				
				//���MARKER_ARRAY_LENGTH������Ӧʱ��
				if (request.getIoTime() > 0){
					totalTime -= timeMarker[marker_perarray];
					timeMarker[marker_perarray] = request.getIoTime();
					totalTime += timeMarker[marker_perarray];
				}
				
				marker++;
				break;
			}
		}
	}
	
	protected boolean shouldClone(){
		//if (!shouldCloneFlag){
		//	return false;
		//}
		synchronized(markerLocker){
			if (totalClone >= pool.getServerConfig().getMaxClonedRequest()){
				return false;
			}
			totalClone++;
		}
		return true;
	}
	
	protected long getServerAvgTime(){
		return getServerAvgTime(0);
	}
	
	protected long getServerAvgTime(int min_requests){
		synchronized(markerLocker){
			if (min_requests <= 0)
				min_requests = MARKER_ARRAY_LENGTH;
			long total = MARKER_ARRAY_LENGTH;
			if (marker < total){
				total = marker;
			}
			if (marker >= min_requests){
				return totalTime/total;
			}
		}
		return 0;
	}
	
}