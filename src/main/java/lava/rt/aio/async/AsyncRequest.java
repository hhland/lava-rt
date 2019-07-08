package lava.rt.aio.async;

import java.lang.ref.WeakReference;

import lava.rt.aio.Request;



public abstract class AsyncRequest implements Request{
	volatile long requestId;
	volatile String serverInfo; 
	protected volatile String ruid = "";   //uniq id per request
	
	public static final int NORMAL_REQUEST = 0;
	public static final int RETRY_REQUEST = 1;
	public static final int SHADOW_NORMAL_REQUEST = -1;
	public static final int SHADOW_QUEUE_REQUEST = -2;
	
	
	// set by pool, to indicate time status 
	protected volatile Long time_start = Long.valueOf(0l); // ���뷢��������е�ʱ��
	
	protected volatile Long time_enqueue = Long.valueOf(0l);
	protected volatile Long time_enqueue_end = Long.valueOf(0l);
	protected volatile Long time_outqueue = Long.valueOf(0l);
	
	protected volatile Long time_waitqueue = Long.valueOf(0l);
	protected volatile Long time_outwaitqueue = Long.valueOf(0l);
	
	protected volatile Long time_connect = Long.valueOf(0l); // ��������
	protected volatile Long time_connect_end = Long.valueOf(0l); // ���ӽ�����ʱ��( ������Ҫ�����������) 
	protected volatile Long time_request = Long.valueOf(0l); // �����socket���ͳ�ȥ��ʱ��
	protected volatile Long time_end = Long.valueOf(0l); // ��Ӧ������ȫ��ʱ��
	protected volatile Long time_ioend = Long.valueOf(0l);
	protected volatile Long endDumpTime = Long.valueOf(0l);
	// set by user thread. to indicate time status
	volatile Long cancelledTime = Long.valueOf(0l);
	
	protected volatile Long startLocktime=Long.valueOf(0l);
	protected volatile Long endLocktime=Long.valueOf(0l);
	
	/**
	 * ����������
	 */
	volatile Object result;
	volatile boolean isResultReady = false;
	volatile boolean isResultReadOnly = false;
	Object resultLock = new Object();
	
	/**
	 * Ϊclone֮���object׼��������request, ��request������¼��Ҫ���͵ĸ�����Ϣ
	 */
	volatile public AsyncRequest clonedFrom = null;
	volatile public AsyncRequest clonedTo = null;
	volatile public AsyncRequest actualRequest = null;
	volatile public boolean clonableRequest = false;
	
	//ʵ�ʴ��������
	public AsyncRequest timedRequest(){
		if (actualRequest != null){
			return actualRequest;
		}
		return this;
	}
	/*
	 * connectTypeȡֵ NORMAL_REQUEST����ͨ����
	 *                RETRY_REQUEST: ���Ƿ��͸��Լ���Ӧ��server �������������Ե�����
	 *                SHADOW_REQUEST: ���ǲ����͸��Լ���Ӧ��server�������ڶ̳�ʱʱ���Ե�����
	 */
	volatile int connectType = NORMAL_REQUEST; 
	public boolean cloned(){
		return clonableRequest&&(clonedFrom!=null||clonedTo!=null);
	}
	
	public AsyncRequest clone(){
		AsyncRequest clone = new AsyncRequest() {
			
			@Override
			public boolean isValid() {
				if (this.clonedFrom != null)
					return this.clonedFrom.isValid();
				return false;
			}
			
			@Override
			public int getServerId(int total) {
				if (this.clonedFrom != null)
					return this.clonedFrom.getServerId(total);
				return -1;
			}
		};
		clone.clonedFrom = this;
		this.clonedTo = clone;
		clone.requestId = this.requestId;
		clone.ruid = this.ruid;
		
		return clone;
	}
	
	/**
	 * �������
	 */
	volatile AsyncServerStatus server = null;
	
	@SuppressWarnings("rawtypes")
	WeakReference ref;
	
	//  0, ��ʼ״̬
	// -1, �Ŷӳ�ʱ
	// -3, �Ƿ�����
	// -2, socket��ʱ
	// -4, ����������
	// -5, connect��ʱ
	//-6, ����ʧ��
	//-7, �û��ȴ���ʱ
	//�����ӡ��޸ġ�ɾ��һ��statusʱ��Ӧ��Ӧ�����ӡ��޸ġ�ɾ��ReturnTypeStatusMap�е�ӳ���ϵ
	public volatile int status = 0;
	String reason;
	
	public final void queueSend(){
		this.time_start = System.currentTimeMillis();
	
	}
//	public void messageSent(){
//		this.time_request = System.currentTimeMillis();
//	}
	public final void messageRecv(){
		this.time_end = System.currentTimeMillis();
	}
	public final void startConnect(){
		this.time_connect = System.currentTimeMillis();
	}
	public final void requestTime(){
		this.time_request = System.currentTimeMillis();
	}
	
	public final void setResult(Object obj){
		//debug bart
		if (this.clonableRequest||this.clonedFrom!=null){
			System.out.println("[pool "+this.ruid+" "+(System.currentTimeMillis()-this.time_start)+"]get result from "+this.getServerInfo()+((this.clonedFrom != null)?" cloned":" orignal"));
		}
		
		synchronized ( resultLock ){
			if (!isResultReady){
				//request��ɣ���¼�����Ϣ
				isResultReady = true;
				this.time_end = System.currentTimeMillis();
				markStats();
				
				//requestδ��Ϊֻ�����޸�result
				if (!isResultReadOnly){
					result = obj;
				}
				
				if (this.clonedFrom != null){
					//��ΪӰ��������Ҫ��ֵ��������
					synchronized ( this.clonedFrom.resultLock ){
						if (!this.clonedFrom.isResultReadOnly){
							this.clonedFrom.result = obj;
						}
						//���ﲢ��ִ�и�ֵ��ֻ���ж��Ƿ����֪ͨ�û��߳�
						this.clonedFrom.requestDone(obj, true);
					}
				}else{
					//���ﲢ��ִ�и�ֵ��ֻ���ж��Ƿ����֪ͨ�û��߳�
					this.requestDone(obj, false);
				}
			}
		}
	
	}
	
	//isResultReadOnlyΪtrue����ʾ�û��ȴ���ʱ�������Ѿ��зǿս��������ԭ�����clone�����ʧ��
	//                        ��Щ�������ʾ��������ɣ���Ҫ֪ͨ�û��̣߳����ҽ�����ʱ
	//isResultReadyΪture����ʾ��request�Ѿ���ɣ�������clone�ڵ�δ������Լ�������
	private final void requestDone(Object obj, boolean fromClone){
		if (!isResultReadOnly){
			boolean done = false;
			
			if (obj != null){
				//�н�����϶�����
				if (fromClone){
					actualRequest = this.clonedTo;
				}
				done = true;
			}else{
				//�ս����������clone�ڵ�ʱ��ֻ�е������ڵ㶼��Ϊ�գ��Ŵ���nofify
				if (fromClone){
					if (this.isResultReady){
						actualRequest = this.clonedTo;
						done = true;
					}
				}else{
					if (this.clonedTo == null || this.clonedTo.isResultReady){
						done = true;
					}
				}
			}
			if (done){
				isResultReadOnly = true;
				resultLock.notify();
			}
		}
	}
	
	/*
	 * ͳ�Ʊ�������
	 */
	private void markStats(){
		if (!clonableRequest&&this.clonedFrom==null){
			return;
		}
		AsyncServerStatus ss = this.server;
		if (ss != null){
			ss.mark(this);
			//debug bart
			//System.out.println("[pool]"+ss.serverInfo+" cloned times: "+ss.totalClone);
			//System.out.println("[pool]"+ss.serverInfo+" avg time: "+ss.getServerAvgTime(1));
		}
	}
	
	public final Object getResult(long time) {
		long currTime = System.currentTimeMillis();
		synchronized( resultLock ){
			if( ! isResultReady && !isResultReadOnly ){
				if( time > 0 ){
					try{
						resultLock.wait(time);
					} catch( InterruptedException e){}
					cancelledTime = System.currentTimeMillis();
				}
				if(result == null && (System.currentTimeMillis() - currTime >= time)) {
					userWaitTimeOut();
				}
			}
			isResultReadOnly = true;
			return result;
		}
	}
	
	/**
	 * callback����
	 * ����������������г�ʱʱ����.
	 *
	 */
	public final void waitTimeout(){
		this.status = -1;
		this.reason = "�Ŷӳ�ʱ";
		this.time_outwaitqueue();
		setResult( null );
	}
	@Override
	public final void serverDown(){
		serverDown("δ֪ԭ��");
	}
	public final void serverDown(String reason){
		this.status = -4;
		this.reason = "������������:"+reason;
		setResult(null);
	}
	public final void illegalRequest(){
		this.status = -3;
		this.reason = "�Ƿ�����";
		setResult(null );
	}
	public final void connectTimeout(){
		this.status = -5;
		this.reason = "���ӳ�ʱ";
		this.time_connect_end();
		setResult(null);
	}
	@Override
	public final void socketTimeout(){
		this.status = -2;
		this.reason = "socket��ʱ";
		this.timeIoend();
		setResult(null);
	}
	public final void invalidResponse(String rr){
		this.status = -8;
		this.reason = "�Ƿ���Ӧ"+rr;
		this.timeIoend();
		setResult(null);
	}
	public final void decodeFailed(){
		this.status = -6;
		this.reason = "����ʧ��";
		this.timeIoend();
		setResult(null);
	}
	public final void userWaitTimeOut() {
		this.status = -7;
		this.reason = "�û��ȴ���ʱ";
	}
	
	public final long getEndTime() {
		return time_end;
	}

	@SuppressWarnings("rawtypes")
	public final WeakReference getRef() {
		return ref;
	}
	@SuppressWarnings("rawtypes")
	public final void setRef(WeakReference ref) {
		this.ref = ref;
	}
	public final long getStartTime() {
		return time_start;
	}

	@Override
	public final String getServerInfo() {
		return serverInfo;
	}

	@Override
	public final long getTime() {
		long ret = getIoTime(Long.MIN_VALUE);
		if( ret == Long.MIN_VALUE ){
			ret = userWaitTime();
		}
		return ret;
	}

	@Override
	public void setRequestId(long id) {
		this.requestId = id;
	}
	
	@Override
	public final long getRequestId(){
		return this.requestId;
	}

	@Override
	public final void setServerInfo(String info) {
		this.serverInfo = info;
	}

	@Override
	public final void setTime(long t) {
		// dummy time��������ģ����ܸ�ֵ��
	}
	public abstract boolean isValid();
	public abstract int getServerId(int total);
	public final long getCancelledTime() {
		return cancelledTime;
	}
	public final void setCancelledTime(long cancelledTime) {
		this.cancelledTime = cancelledTime;
	}
	public final long getConnectEndTime() {
		return time_connect_end;
	}
	public final long getEndDumpTime() {
		return endDumpTime;
	}
	public final long getRequestTime() {
		return time_request;
	}
	
	public final String dumpTimeStatus(){
		return ( "status: " + status + ", start: "+ time_start
				+ ", wait_q:" + ((time_outwaitqueue==0)?0:(time_outwaitqueue-time_start))
				+ ", q:" + ((time_enqueue==0)?0:(time_enqueue-time_start))
				+ ", q_end:" + ((time_enqueue_end==0)?0:(time_enqueue_end-time_start))
				+ ", q_out:" + ((time_outqueue==0)?0:(time_outqueue-time_start))
				+ ", req: " +  ((time_request==0)?0:(time_request-time_start))
				+ ", ioend:" + getIoTime()
				+ ", conn:" + ((time_connect==0)?0:(time_connect-time_start))
				+ ", conn_end:" + ((time_connect_end==0)?0:(time_connect_end-time_start))
				+ ", end: " +  ((time_end==0)?0:(time_end-time_start))
				+ ", userEnd:" + ((cancelledTime==0)?0:(cancelledTime - time_start))
				+ ", reason:" + reason
				);

	}
	public final AsyncServerStatus getServer() {
		return server;
	}
	public final void setServer(AsyncServerStatus server) {
		this.server = server;
	}
	public final void timeIoend(){
		this.time_ioend = System.currentTimeMillis();
	}
	public final long getIoTime(){
		return getIoTime(0);
	}
	public final long getIoTime(long defaultValue){
		if( time_request > 0 ){
			if( time_ioend > 0 ){
				return time_ioend - time_request;
			} else {
				return System.currentTimeMillis() - time_request;
			}
		} else {
			return defaultValue;
		}
	}
	public final long getConnectTime(){
		return ((time_connect_end==0||time_connect==0)?0:(time_connect_end-time_connect));
	}

	public final void time_connect() {
		this.time_connect = System.currentTimeMillis();
	}

	public final void time_enqueue() {
		this.time_enqueue = System.currentTimeMillis();
	}

	public final void time_enqueue_end() {
		this.time_enqueue_end = System.currentTimeMillis();
	}

	public final void time_outqueue() {
		this.time_outqueue =System.currentTimeMillis();
	}
	public final void time_connect_end(){
		this.time_connect_end = System.currentTimeMillis();
	}
	public final void time_waitqueue() {
		this.time_waitqueue =System.currentTimeMillis();
	}
	public final void time_outwaitqueue(){
		this.time_outwaitqueue = System.currentTimeMillis();
	}
	public final void setQueueTime(long t ){
		
	}
	public final long getQueueTime(){
		if( this.time_request > 0 ){ // �����Ѿ�����
			return this.time_request - this.time_start;
		} else if( this.time_waitqueue > 0 ){ // ����������
			if( this.time_outqueue > 0 ){
				return this.time_outqueue - this.time_start;
			} else if( this.time_enqueue > 0 ){
				return this.time_enqueue - this.time_start;
			} else if( this.time_outwaitqueue > 0 ){
				return this.time_outwaitqueue - this.time_start;
			} else {
				return System.currentTimeMillis() - this.time_start;
			}
					
		} else if( this.time_enqueue > 0 ) { // �����Ѿ�����ע�����
			if( this.time_outqueue > 0 ){
				return this.time_outqueue - this.time_start;
			} else {
				return System.currentTimeMillis() - this.time_start;
			}
		} else {
			return 0;
		}
	}
	public final long userWaitTime(){
		if( time_start > 0 ){
			
			if( cancelledTime > 0 ){
				return cancelledTime - time_start; 
			} else {
				return System.currentTimeMillis() - time_start;
			}
		} else {
			return 0;
		}
	}
	@Override
	public final int getStatus() {
		return status;
	}
	@Override
	public final void setStatus(int status) {
		this.status = status;
	}

	public boolean isResultReady() {
		return isResultReady;
	}

	public void setResultReady(boolean isResultReady) {
		this.isResultReady = isResultReady;
	}

	public boolean isResultReadOnly() {
		return isResultReadOnly;
	}

	public void setResultReadOnly(boolean isResultReadOnly) {
		this.isResultReadOnly = isResultReadOnly;
	}
	
	public void startLocktime(){
		this.startLocktime=System.currentTimeMillis();
	}
	
	public void endLocktime(){
		this.endLocktime=System.currentTimeMillis();
	}
	
	public long getLocktime(){
		if(startLocktime>0l&&endLocktime>0l){
			return endLocktime-startLocktime;
		}else if(startLocktime>0l){
			return System.currentTimeMillis()-startLocktime;
		}else{
			return 0l;
		}		
	}
}
