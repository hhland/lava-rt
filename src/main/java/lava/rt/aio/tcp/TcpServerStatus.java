package lava.rt.aio.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import lava.rt.logging.Logger;
import lava.rt.logging.LoggerFactory;


public class TcpServerStatus {
	
	private static Logger logger = LoggerFactory.SYSTEM.getLogger(TcpServerStatus.class);

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
	
	protected LinkedList<TcpRequest> waitQueue = new LinkedList<>();
	protected LinkedList<TcpQueryClient> freeChannelList;
	ArrayList<TcpQueryClient> allClients;
	
	
	TcpConnectionPool pool;
	
	

	/**
	 * called by Receiver to check if any client has reached a socket timeout
	 *
	 */
	public void checkTimeout (){
		long now = System.currentTimeMillis();
		for( int i=0; i< allClients.size();i++){
			TcpQueryClient c = allClients.get(i);
			
			do{
				if( !c.isValid() ) break;
				
				// check if Clinet is free.
				if( ! c.using ) break;
				
				
				// check time value.
				boolean isSocketTimeout = true;
				if( ( !c.requestSent
						|| c.getTime_request() == 0
						|| 	now - c.getTime_request() <= pool.getServerConfig().socketTimeout )
						){
					isSocketTimeout = false;
				}
				boolean isConnectTimeout = true;
				if( ( c.isConnected() 
						|| c.getTime_connect() == 0
						|| now - c.getTime_connect() <= pool.getServerConfig().connectTimeout ) )
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
						|| 	now - c.getTime_request() <= pool.getServerConfig().socketFailTimeout )
						){
					isSocketFailTimeout = false;
				}
				
				
				
				TcpRequest request = c.getRequest();			
				if (isConnectTimeout || isSocketFailTimeout)
				{
					//�����һ������ʧ���˵�����
					
						logger.info("Socket TimeOut!!!"+c.requestSent+" "+c.getTime_request()
								+ " "+(now - c.getTime_request())+" "+pool.getServerConfig().socketTimeout 
								+ "\nIO TimeOut!!!"+c.isConnected()+" "+c.getTime_connect()
								+ " "+(now - c.getTime_connect())+" "+ pool.getServerConfig().connectTimeout);
					
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
							TcpRequest request_cloned = request.clone();
							request_cloned.connectType = TcpRequest.SHADOW_NORMAL_REQUEST;
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
	int serverSendRequest(TcpRequest request) {
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
//				logger.info("put a request in the queue");
//			}
			return 1;
		} else {
			int sts = innerSendRequest( request );
			
			if( sts > 0 ){
//				if( logger.isTraceEnabled() ){
//					logger.info("No avaliable channal put request in the queue");
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
		TcpRequest request = firstUserRequest();
		
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
	int innerSendRequest(TcpRequest request) {
		
		if (!isServerAlive() && request.connectType != TcpRequest.RETRY_REQUEST) {
			
				logger.info("server is down, Don't waste Time");
			
			request.serverDown("[innerSend]������������");
			return -2;
		}
		
		long now = System.currentTimeMillis();
		
		if (now - request.getStartTime() > pool.getServerConfig().queueTimeout) { // �Ŷӳ�ʱ
			
				logger.info("WaitQueue timeOut");
			
			queueTimeout();
			request.waitTimeout();
			return -4;
		}
		
		if (now - request.getStartTime() > pool.getServerConfig().queueShortTimeout) { // �ŶӶ̳�ʱ
			//�ŶӶ̳�ʱ����
				//debug bart
				//�����Ŷӳ�ʱ��ת��
				if (shouldCloneFlag){
					LinkedList sendQueue = new LinkedList();
					synchronized( waitQueue){
						for( int i=0; i<waitQueue.size(); i++){
							TcpRequest req = waitQueue.get(i);
							if (now - req.getStartTime() > pool.getServerConfig().queueShortTimeout){
								if (req.clonableRequest && req.clonedTo == null && req.clonedFrom == null){
									System.out.println("[pool "+req.ruid+"]Short queue timeout is trigged for "+req.getServerInfo());
									//��request����clone��������δ��clone
									TcpRequest request_cloned = req.clone();
									request_cloned.connectType = TcpRequest.SHADOW_QUEUE_REQUEST;
									sendQueue.addLast(request_cloned);
								}
							}
						}
					}
					for( int i=0; i<sendQueue.size(); i++){
						TcpRequest req = (TcpRequest)sendQueue.get(i);
						pool.sendRequest(req);
					}
				}
		}

		do{
			TcpQueryClient sc = removeFirstFreeClient();

			if (sc == null){
				
					logger.info("CLIENT RUNOUT!");
				
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
						
							logger.info("Sender: open new SocketChannel ");
						
					}
					if ( !isClientValid ) {
						request.serverDown("[innSend]����ʧ��");
						freeClient(sc);
						noResourceError();
						return -3;
					}
				}
				
				if (!sc.isConnected()) { // �����첽��������
					
						logger.info("NOT CONNECTED!");
					
					sc.setRequest(request);

					sc.requestSent(false);
					request.time_enqueue();
					pool.recver.queueChannel(sc);
					pool.wakeup();
					request.time_enqueue_end();
					return 0;
				}
			}

			sc.setRequest(request);

			// ��������
			
				logger.info("TO SEND REQ!");
			

			request.time_connect_end();

			int status = -1;
			try {
				status = sc.sendRequest();
			} catch (IOException e) {
				sendError();
				
					logger.info("IOE");
				
				sc.close();
			} catch (RuntimeException e) {
				
					logger.info("RTE While sending Request(Non-IOE)");
				
			} catch (Exception e) {
				
					logger.info("Exception While sending Request(Non-IOE)");
				
			}

			if (status > 0) {

				/**
				 * ���ͳɹ�, ���channel�Ƿ��Ѿ�ע��. ��Ϊǰ��finishConnect�����Ѿ����ӳɹ�,
				 * ������ֱ�ӷ���,Ȼ�����������ע������.
				 */
				
					logger.info("SENT SUCCESS!");
				

				request.requestTime();
				sc.setTime_request();
				sc.requestSent(true);

				if (!isRegistered) {
					
						logger.info("NOT REGED!");
					
					request.time_enqueue();
					pool.recver.queueChannel(sc);
					pool.wakeup();
					request.time_enqueue_end();
				}

				return 0;
			} else {
				/**
				 * ���Ͳ��ɹ� �����ֿ���: socket��request. �����socket������,
				 * ��ôrecver����ȷ����(close). �����request������, ��ô��֪ͨ�û��̼߳���.
				 */
				
					logger.info("ILLEGAL REQUEST!");
				
				if(status == 0) {
					request.illegalRequest();
				}else {
					request.serverDown("[innSnd]����ʧ��");
				}
				try {
					sc.reset();
				} catch (Exception e) {
					
						logger.info("Exception while reset client");
					
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
	
	private TcpQueryClient removeFirstFreeClient(){
		TcpQueryClient client;
		synchronized( freeChannelList ){
			if( freeChannelList.size() > 0 ){
				
					logger.info("Get One Client From " + freeChannelList.size());
				
				client = freeChannelList.removeFirst( );
				client.using = true;
				client.requestSent = false;
			} else {
				
					logger.info("no Free Client to get");
				
				return null;
			}
		}
		return client;
	}
	void freeClient(TcpQueryClient client) {
		synchronized (freeChannelList) {
			if (client.using) {
				
					logger.info("Free a Client");
				
				client.using = false;
				freeChannelList.addFirst(client);
			}
		}
		/**
		 * @TODO check Sender Thread
		 */
		this.pool.sender.checkSenderThread();
	}

	private TcpRequest firstUserRequest(){
		synchronized( waitQueue ){
			if( waitQueue.isEmpty() ){
				return null;
			} else {
				return this.waitQueue.element();
			}
		}
	}

	private TcpRequest removeFirstUserRequest(){
		TcpRequest client;
		synchronized( waitQueue ){
			client = waitQueue.removeFirst( );
		}
		
			logger.info("Remove a request from the queue");
		
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
				&& (now - downtime ) >= pool.getServerConfig().shortRetryTime ))
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
	
	public TcpServerStatus(String line, TcpConnectionPool pool ) throws IllegalArgumentException{
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
		int count = pool.getServerConfig().maxConnectionsPerServer;
		ArrayList al = new ArrayList( count );
		LinkedList deactiveChannelSet = new LinkedList();
		for(int i=0;i<count;i++){
			
			TcpQueryClient ace = pool.factory.newInstance();
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
		
			logger.info("errNumber is " + recentErrorNumber );
		
		this.recentErrorNumber ++;
		System.out.println("[pool connectError]"+recentErrorNumber+"\t"+serverInfo);
		this.downtime = System.currentTimeMillis();
	}
	public synchronized void socketTimeout() {
		
			logger.info("sockettimeout "+ recentErrorNumber);
		
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

	protected final int queueRequest(TcpRequest request){
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
				TcpRequest req = waitQueue.get(i);
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
			TcpQueryClient ace = this.allClients.get(i);
			sb.append( ace.getStatus() );
			sb.append('\n');
		}
		
		
			logger.info( sb.toString() );
		
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
		
		for( int i=0; i< allClients.size(); i++){
			TcpQueryClient ace = allClients.get(i);
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
	
		for( int i=0; allClients != null && i< allClients.size();i++){
			TcpQueryClient c = allClients.get(i);
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
	
	protected void mark(TcpRequest request){
		synchronized(markerLocker){
			while(true){
				//��������
				if (request.connectType == TcpRequest.RETRY_REQUEST){
					retryCount--;
				}
				//ʧ�ܵ����󲻽���ͳ��?
				//if (request.status < 0){
				//	break;
				//}
				
				//���MARKER_ARRAY_LENGTH�������б�clone��ȥ�ز�Ĵ���
				boolean isClone = (request.clonedTo != null && request.clonedTo.connectType == TcpRequest.SHADOW_NORMAL_REQUEST);
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
			if (totalClone >= pool.getServerConfig().maxClonedRequest){
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