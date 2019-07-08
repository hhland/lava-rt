/**
 * @author Mingzhu Liu (mingzhuliu@sohu-inc.com)
 * 
 *  Created on 2006-09-23
 *   
 */
package lava.rt.aio.async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import lava.rt.logging.Log;




public abstract class AsyncGenericQueryClient {

	public static final int STS_OPEN = 0;
	public static final int STS_CONN = 1;
	public static final int STS_BUSY = 2;
	public static final int STS_FREE = 3;
	public static final int STS_CLOS = 4;
	
	volatile Long time_connect = 0l;
	volatile Long time_request = 0l;
//	boolean needRequest = false;
	boolean requestSent = true;
	
	public long getTime_connect() {
		return time_connect;
	}

	public void setTime_connect() {
		this.time_connect = System.currentTimeMillis();
	}

	public long getTime_request() {
		return time_request;
	}

	public void setTime_request() {
		this.time_request = System.currentTimeMillis();
	}

	public int getStatus(){
		SocketChannel channel;
		boolean using;
		synchronized( channelLock) {
			channel = this.channel;
			using = this.using;
		}
			if( channel == null || !channel.isOpen() ){
				return STS_CLOS;
			} else if( ! channel.isConnected() ){
				return STS_OPEN;
			} else if( channel.isConnectionPending() ){
				return STS_CONN;
			} else if( !using ){
				return STS_FREE;
			} else {
				return STS_BUSY;
			}
	}
	
	//private Log logger = getLogger();

	private Object channelLock = new Object();
	private volatile SocketChannel channel = null;
	protected AsyncServerStatus serverStatus;
	
	protected boolean using = false;
	
	
	protected int life = 0;
	
	private volatile AsyncRequest request ;
	
	public void setLife(int lf) {
		this.life = lf;
	}
	
	public int getLife(){
		return this.life;
	}

	
	final public void close() {
		SocketChannel channel;
		synchronized( channelLock ){
			channel = this.channel;
			this.channel = null;
		}
		if (channel != null) {
			try {
				channel.close();
			} catch (IOException e) {
				//logger.debug(this, e);
			}
		}
	}

	public void finalize(){
		close();
	}

	public boolean isValid() {
		synchronized( channelLock ){
			return ( channel != null 
					&& channel.isOpen()	 );
		}
	}
	public boolean isRegistered() {
		synchronized( channelLock ){
			return ( channel != null 
					&& channel.isRegistered()	 );
		}
	}
	
	public boolean isConnected(){
		synchronized( channelLock ){
			return ( channel != null 
					&& channel.isConnected() );
		}
	}

	public boolean isActive(){
		synchronized( channelLock ){
			return ( channel != null 
					&& channel.isOpen()
					&& channel.isConnected() );
		}
	}
	
	public boolean isConnectionPending(){
		synchronized( channelLock ){
			return ( channel != null 
					&& channel.isConnectionPending() );
		}
		
	}


	/**
	 * callback function, �����ӳص�Receiver�̵߳��ã�ÿ��ʵ�����ӳص�ʵ���߱���ʵ�ִ˷�����
	 * ��һ����Ӧ���ݵĽ��ܹ����У��÷����ᱻ��ε��á�ʵ�ֽ�����д��Buffer�Ĺ��̡�
	 * �������ӳ����಻����client�˵�Buffer����ʵ�ַ�ʽ�����Ա���ÿ��������������ɡ�
	 * ����ע�⣺��Ҫ�����е����������
	 * @return true  - if a complete reponse body has been received
	 * @throws Exception  - IOException is thrown if there is any IO problem.
	 *                      or NullPointerException for hell code.
	 */
	protected abstract int handleInput() throws IOException;
	/**
	 * callback function , by Sender
	 * @param request
	 * @return bytes sent.
	 *  >0 request has been successfully sent
	 *  =0 request needn't to be sent
	 *  <0 some Exception happeds, need reset
	 */
	public abstract int sendRequest() throws IOException;
	/**
	 * callback function, �����ӳص�Receiver�̵߳��ã�ÿ��ʵ�����ӳص�ʵ���߱���ʵ�ִ˷�����
	 * ��һ����Ӧ���ݽ��չ����У��÷����ᱻ��ε��ã�ʵ���߱���ÿ�ζ�Ҫ�����������Ѿ�������
	 * �ǵĻ����ͷ���true�����򷵻�false��ͬʱע�⣬Buffer��������״̬�ģ���Ҫ���Ѿ����յ������ݸ��ǵ���
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean finishResponse() throws IOException;

	/**
	 * callback function, by receiver or sender, if some Exceptions thrown.
	 * client���ͷź󱻵��ã�����ִ������������Ա���һ�������á�һ�������������롢���buffer�����������
	 *
	 */
	public abstract void reset() throws IOException;
	

	public void connect(InetSocketAddress addr) throws IOException {
		SocketChannel channel = SocketChannel.open(); 
		channel.configureBlocking(false);
		channel.connect(addr);
		channel.finishConnect();
		
		synchronized( channelLock ){
			this.channel = channel;
		}
	}
	protected abstract Log getLogger();

	public AsyncRequest getRequest() {
		return request;
	}

	
	public void setRequest(AsyncRequest request) {
		this.request = request;
	}

	/**
	 * called by Receiver. when getting data from recver-queue.
	 * to determin if it has send request by itself
	 * @return
	 */
	public boolean requestSent() {
		return requestSent;
	}

	/**
	 * set by user-thread. before putting request to recver-queue.
	 * @param needRequest
	 */
	public void requestSent(boolean needRequest) {
		this.requestSent = needRequest;
	}

	public SocketChannel getChannel() {
		return channel;
	}

//	public void setChannel(SocketChannel channel) {
//		this.channel = channel;
//	}
//
}
