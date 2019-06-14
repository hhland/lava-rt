package lava.rt.pool.impl;

import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;

class AsyncSender implements Runnable{

	private static final Log log = LogFactory.SYSTEM.getLog(AsyncSender.class);
	
	private String generation = "(ThreadErr)"; 
	private volatile Thread _thread = null;
	AsyncGenericConnectionPool pool;
	
	private long yieldTime = 50;
	private long minSleepTime = 500;
	private long maxSleepTime = 1200000l;
	private long sleepTime;
	
	private long requestCount = 0;
	private Object requestCountLock = new Object();
	
	private boolean needRestart = false;
	
	private static int GENERATION = 0;
	private static Object GENERATION_LOCK = new Object();
	private static int newGeneration(){
		int ret = 0;
		synchronized( GENERATION_LOCK ){
			ret = ++ GENERATION;
		}
		return ret;
	}
	
	
	AsyncSender( AsyncGenericConnectionPool sc ){
		this.pool = sc;
	}
	
	/**
	 * �߳�״̬���
	 * @return true - ��������̻߳��ڴ��.
	 */
	public boolean isAlive(){
		return this._thread !=null && this._thread.isAlive();
	}
	
	/**
	 * external API.
	 *  called by Receiver or User-Thread
	 *  to make sure the Sender Thread is Alive
	 */
	public void checkSenderThread(){
		boolean toRestart = false;
		synchronized( requestCountLock ){
			if( requestCount > 0 ){
				toRestart = needRestart;
				if( needRestart ){
					needRestart = false;
				}else{
					requestCountLock.notifyAll();
				}
			}
		}
		if( toRestart ){
			startThread();
		}
	}
	/**
	 * External API.
	 *  called by Anyone at anytime
	 *  to get a snapshot of request count num.
	 * @return
	 */
	public long getRequestCount(){
		return requestCount;
	}

	/**
	 * �߳�����
	 *
	 */
	public void startThread(){
		sleepTime = minSleepTime;
		this.generation = pool.getServerConfig().name+"(Sender" + newGeneration() + ")";
		_thread = new Thread(this, this.generation);
		_thread.start();
	}
	/**
	 * �߳�ֹͣ
	 * *ע��* ����stopThread������ζ���߳�����ֹͣ
	 *
	 */
	public void stopThread(){
		_thread = null;
	}
	
	public void run() {
		while (true) {
			if( _thread != Thread.currentThread() ){
				
					log.info( this.generation + "EXIST. NOT CURRTHREAD");
				
				break;
			}
			long cycleStart = System.currentTimeMillis();
			
				log.info(generation +"CycleStart:time:"+cycleStart );
			
			boolean doneSomething = false;
			do{
				
				AsyncServerStatus[] sss = null;
				sss = pool.getAllStatus();
				if (sss == null) {
					break;
				}
				
				for (int i = 0; i < sss.length; i++) {
					long now = System.currentTimeMillis();
					
						log.info(generation + "CheckServer:"+i+" ,time:"+now);
					
					AsyncServerStatus ss = sss[i];
					if (ss == null){
						
							log.info(generation + "CheckServer:"+i+",ServerStatus is NULL, toContinue");
						
						continue;
					}

					while(true) {

						
							log.info(generation + "CheckServer:"+i+",to innerSendRequest()");
						
						int status = ss.innerSendRequest();
						
							log.info(generation + "CheckServer:"+i+",innerSendRequest() returns " + status);
						

						if (status == 1) {
							// ��������ȫ���ѱ�ռ��, ��û������
							break;
						} else if (status <= 0) {
							synchronized (requestCountLock) {
								requestCount--;
							}
							doneSomething = true;
						}
					}
					
						log.info(generation +"SenderServerEnd:"+i+" ,time:"+(System.currentTimeMillis()-now) );
					
					Thread.yield();
				}
			} while (false);
			
			if (doneSomething) {
				sleepTime = minSleepTime;
			}
			
				log.info(generation +"CycleEnd:time:"+(System.currentTimeMillis()-cycleStart)
						+ ",sleepTime:"	+ sleepTime + ",minSleepTime:"  + minSleepTime 
						+ ",maxSleepTime:" + maxSleepTime );
			
			
			// spare-time task
			if (sleepTime >= maxSleepTime) {
				
					log.info(generation +"sleepTime Too Long, to Exsit Thread");
				}
				synchronized (requestCountLock) {
					if( requestCount == 0 ){
						needRestart = true;
						_thread = null;
						return;
					}
				}
				
					log.info(generation +"sleepTime Too Long, doesn't Exsit Thread");
				
				sleepTime = minSleepTime;
			}
			try {
				long now = System.currentTimeMillis();
				
					log.info(generation +"CheckSleep(2),requestCount:" + requestCount + ",start:" + now);
				
				synchronized( requestCountLock ){
					if( requestCount == 0 ){
						requestCountLock.wait(sleepTime);
						sleepTime <<= 1;
					} else {
						requestCountLock.wait(yieldTime);
					}
				}
				
					log.info(generation +"CheckSleep(2)End, time:" + (System.currentTimeMillis()-now) );
				
			} catch (InterruptedException e) {
				// δ֪״��, ��֪������ô��.
				Thread.interrupted();
				_thread = null;
				needRestart = true;
				e.printStackTrace();
				
				log.info(generation +"Interrupted. Farewell");
				
			
			
				//log.info(generation +"CycleLoopback, time:" + (System.currentTimeMillis()-cycleStart) );
			
		}
	}
	/**
	 * ��������
	 * ���û��߳�ͨ��AsyncConnectionPool.sendRequest����
	 * @param request
	 * @return
	 */
	int senderSendRequest( AsyncRequest request){
		
		AsyncServerStatus ss = request.getServer();
		
		assert( ss != null );
		
		int ret = ss.serverSendRequest(request);
		
		if( ret == 1 ){
			synchronized( requestCountLock ){
				requestCount ++;
			}
			
				log.info(this.generation+"request Count is "+ requestCount);
			
			checkSenderThread();
		}
		
		return ret;
	}


	public long getMaxSleepTime() {
		return maxSleepTime;
	}


	public void setMaxSleepTime(long maxSleepTime) {
		this.maxSleepTime = maxSleepTime;
	}


	public long getMinSleepTime() {
		return minSleepTime;
	}


	public void setMinSleepTime(long minSleepTime) {
		this.minSleepTime = minSleepTime;
	}


	public long getYieldTime() {
		return yieldTime;
	}


	public void setYieldTime(long yieldTime) {
		this.yieldTime = yieldTime;
	}
	
}
