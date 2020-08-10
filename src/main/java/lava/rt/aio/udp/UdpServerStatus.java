package lava.rt.aio.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import lava.rt.logging.Logger;
import lava.rt.logging.LogFactory;


public class UdpServerStatus {
	
	private static Logger logger = LogFactory.SYSTEM.getLog(UdpServerStatus.class);

	Object key;
	
	/**
	 * Runtime Status
	 */
	volatile int recentErrorNumber;
	volatile Long downtime = 0l;
	
	/**
	 * Configuration
	 */
	InetSocketAddress addr;
	String serverInfo;
	
	protected LinkedList<UdpRequest> waitQueue = new LinkedList<>();
	protected LinkedList<UdpQueryClient> freeChannelList;
	ArrayList<UdpQueryClient> allClients;
	
	UdpConnectionPool pool;
	
	

	/**
	 * called by Receiver to check if any client has reached a socket timeout
	 *
	 */
	public void checkTimeout (){
		long now = System.currentTimeMillis();
		for( int i=0; i< allClients.size();i++){
			UdpQueryClient c = allClients.get(i);
			
			do{
				if( !c.isValid() ) break;
				
				// check if Clinet is free.
				if( ! c.using ) break;

				// check time value.
				if( ( !c.requestSent
						|| c.getTime_request() == 0
						|| 	now - c.getTime_request() <= pool.serverConfig.socketTimeout )
					 )
				{
//					System.out.println("Socket or IO not timeOut"+c.requestSent);
					break;
				}
				
				
					logger.info("Socket TimeOut!!!"+c.requestSent+" "+c.getTime_request()
							+ " "+(now - c.getTime_request())+" "+pool.serverConfig.socketTimeout
							+ "\nIO TimeOut!!! "+c.getTime_connect()
							+ " "+(now - c.getTime_connect())+" "+ pool.serverConfig.connectTimeout);
				
				UdpRequest request = c.getRequest();
				if( request != null ){
					request.socketTimeout();
				}
				c.serverStatus.socketTimeout();
				
				c.close();
				try{
					c.reset();
				}catch( Exception e){
					// ���Լ���
				}
				freeClient( c );
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
	int serverSendRequest(UdpRequest request) {
		// ����������Ϸ���, �ͼ���ȴ�����.
		boolean needQueue = false;
		
		synchronized( waitQueue ){
			if( this.waitQueue.size() > 0 ){
				needQueue = true;
			}
		}
		if( needQueue ){			
			queueRequest( request );
			
				logger.info("put a request in the queue");
			
			request.setConnectionErrorStatus(1);
			return 1;
		} else {
			int sts = innerSendRequest( request );
			
			if( sts > 0 ){
				
					logger.info("No avaliable channal put request in the queue");
				
				queueRequest( request );
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
		UdpRequest request = firstUserRequest();
		
		if( request == null ) return 1;
		
		sts = innerSendRequest( request );
		request.time_trySend();
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
	int innerSendRequest(UdpRequest request) {
		
		if (!isServerAvaliable() && !request.isProbe()) {
			
				logger.info("server is down, Don't waste Time");
			
			request.serverDown();
			request.setConnectionErrorStatus(-2);
			return -2;
		}
		
		long now = System.currentTimeMillis();
		
		if (now - request.getStartTime() > pool.serverConfig.queueTimeout) { // �Ŷӳ�ʱ
			
				logger.info("WaitQueue timeOut");
			
			request.waitTimeout();
			request.setConnectionErrorStatus(-4);
			return -4;
		}

		do{
			UdpQueryClient sc = removeFirstFreeClient();

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
						sc.setTime_connect();
						request.time_connect();
						sc.connect(addr);
						request.time_connect_end();
						isClientValid = true;
					} catch (IOException e) {
						
							logger.info("Sender: open new SocketChannel ");
						
					}
					if ( !isClientValid ) {
						request.serverDown();
						freeClient(sc);
						noResourceError();
						request.setConnectionErrorStatus(-3);
						return -3;
					}
				}
				
			}

			sc.setRequest(request);

			// ��������
			
				logger.info("TO SEND REQ!");
			

			request.time_connect_end();

			int status = -1;
			try {
				request.requestTime();
				sc.setTime_request();
				status = sc.sendRequest();
			} catch (IOException e) {
				sendError();
				
				sc.close();
			} catch (RuntimeException e) {
				
					logger.error("RTE While sending Request(Non-IOE)");
				
			} catch (Exception e) {
				
					logger.error("Exception While sending Request(Non-IOE)");
				
			}

			if (status > 0) {

				/**
				 * ���ͳɹ�, ���channel�Ƿ��Ѿ�ע��. ��Ϊǰ��finishConnect�����Ѿ����ӳɹ�,
				 * ������ֱ�ӷ���,Ȼ�����������ע������.
				 */
				
					logger.info("SENT SUCCESS!");
				

				sc.requestSent(true);

				if (!isRegistered) {
					
						logger.info("NOT REGED!");
					
					request.time_enqueue();
					pool.recver.queueChannel(sc);
					pool.wakeup();
					request.time_enqueue_end();
				}
				request.setConnectionErrorStatus(0);	
				return 0;
			} else {
				/**
				 * ���Ͳ��ɹ� �����ֿ���: socket��request. �����socket������,
				 * ��ôrecver����ȷ����(close). �����request������, ��ô��֪ͨ�û��̼߳���.
				 */
				
					logger.info("ILLEGAL REQUEST!");
				

				request.illegalRequest();
				try {
					sc.reset();
				} catch (Exception e) {
					
						logger.info("Exception while reset client");
					
					// ignore
				}
				freeClient(sc);
				request.setConnectionErrorStatus(-1);
				return -1;
			}
			
		} while( false );
		request.setConnectionErrorStatus(1);
		return 1;
	}
	
	private UdpQueryClient removeFirstFreeClient(){
		UdpQueryClient client;
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
	void freeClient(UdpQueryClient client) {
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

	private UdpRequest firstUserRequest(){
		synchronized( waitQueue ){
			if( waitQueue.isEmpty() ){
				return null;
			} else {
				return this.waitQueue.element();
			}
		}
	}

	private UdpRequest removeFirstUserRequest(){
		UdpRequest client;
		synchronized( waitQueue ){
			client = waitQueue.removeFirst( );
		}
		
			logger.info("Remove a request from the queue");
		
		return client;
	}

	public boolean isServerAvaliable(){
		long now = System.currentTimeMillis();
		int queueSize = 0;
		synchronized( waitQueue ){
			queueSize = waitQueue.size();
		}
		return (( recentErrorNumber <= pool.serverConfig.maxErrorsBeforeSleep
					|| (now - downtime ) >= pool.serverConfig.sleepMillisecondsAfterTimeOutError 
				) && queueSize <= pool.serverConfig.maxQueueSize);
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
	
	public UdpServerStatus(String line, UdpConnectionPool pool ) throws IllegalArgumentException{
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
		int count = pool.serverConfig.maxConnectionsPerServer;
		ArrayList<UdpQueryClient> al = new ArrayList<>( count );
		LinkedList<UdpQueryClient> deactiveChannelSet = new LinkedList<>();
		for(int i=0;i<count;i++){
			
			UdpQueryClient ace = pool.factory.newInstance();
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
		this.downtime = System.currentTimeMillis();
	}
	public synchronized void socketTimeout() {
		
			logger.info("sockettimeout "+ recentErrorNumber);
		
		this.recentErrorNumber ++;
		this.downtime = System.currentTimeMillis();
	}
	public synchronized void sendError() {
		this.recentErrorNumber ++;
		this.downtime = System.currentTimeMillis();
	}
	public synchronized void noResourceError() {
		this.recentErrorNumber ++;
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

	protected final int queueRequest(UdpRequest request){
		request.time_waitqueue();
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
				UdpRequest req = waitQueue.get(i);
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
		sb.append( ", ava:");
		sb.append( this.isServerAvaliable() );
		sb.append( ", free:");
		sb.append( this.freeChannelList.size() );
		sb.append( '\n' );
		
		for( int i=0; i< this.allClients.size(); i++){
			sb.append( '\t' );
			UdpQueryClient ace = this.allClients.get(i);
			sb.append( ace.getStatus() );
			sb.append('\n');
		}
		
		
			logger.info( sb.toString() );
		
		return sb.toString();
	}
	public void finalize(){
		destroy();
	}
	/**
	 * �����ͷ���Դ
	 */
	public void destroy(){
		//ArrayList allClients = this.allClients;
		for( int i=0; allClients != null && i< allClients.size();i++){
			UdpQueryClient c = allClients.get(i);
			c.close();
		}
	}
}