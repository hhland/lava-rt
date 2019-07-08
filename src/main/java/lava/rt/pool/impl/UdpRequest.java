package lava.rt.pool.impl;

import java.lang.ref.WeakReference;

import lava.rt.aio.Request;



public abstract class UdpRequest implements Request{
	Object identityObj;
	long requestId;
	String serverInfo;
	int serverId;
	boolean isProbe = false;
	
	/**
	 * ͳ��ʱ�����
	 */
	// set by pool, to indicate time status 
	protected Long time_start = Long.valueOf(0l); // ���뷢��������е�ʱ��
	
	protected Long time_enqueue = Long.valueOf(0l);
	protected Long time_enqueue_end = Long.valueOf(0l);
	protected Long time_outqueue = Long.valueOf(0l);
	
	protected Long time_waitqueue = Long.valueOf(0l);
	protected Long time_outwaitqueue = Long.valueOf(0l);
	
	protected Long time_connect = Long.valueOf(0l); // ��������
	protected Long time_connect_end = Long.valueOf(0l); // ���ӽ�����ʱ��( ������Ҫ�����������) 
	protected Long time_trySend = Long.valueOf(0l); // sender��ͼ���͵�ʱ��
	protected Long time_request = Long.valueOf(0l); // �����socket���ͳ�ȥ��ʱ��
	protected Long time_handleInput = Long.valueOf(0l); // ���һ�ν�����Ӧ��ʱ��
	protected Long time_end = Long.valueOf(0l); // ��Ӧ������ȫ��ʱ��
	protected Long time_ioend = Long.valueOf(0l);
	protected Long endDumpTime = Long.valueOf(0l);
	// set by user thread. to indicate time status
	Long cancelledTime = Long.valueOf(0l);
	
	protected  Long startLocktime=Long.valueOf(0l);
	protected  Long endLocktime=Long.valueOf(0l);
	
	
	/**
	 * ����������
	 */
	Object result;
	boolean isResultReady = false;
	boolean isResultReadOnly = false;
	Object resultLock = new Object();
	
	/**
	 * �������
	 */
	UdpServerStatus server = null;
	
	WeakReference ref;
	
	//  0, ��ʼ״̬
	// -1, �Ŷӳ�ʱ
	// -3, ���ֽڷ���
	// -2, socket��ʱ
	// -4, ����������
	// -5, connect��ʱ
	public int status = 0;
	String reason;
	
	public void queueSend(){
		this.time_start = System.currentTimeMillis();
	
	}
//	public void messageSent(){
//		this.time_request = System.currentTimeMillis();
//	}
	public void messageRecv(){
		this.time_end = System.currentTimeMillis();
	}
	public void startConnect(){
		this.time_connect = System.currentTimeMillis();
	}
	public void requestTime(){
		this.time_request = System.currentTimeMillis();
	}
	
	public void setResult(Object obj){
		this.time_end = System.currentTimeMillis();
		synchronized ( resultLock ){
			if( ! isResultReadOnly ){
				result = obj;
				isResultReady = true;
				isResultReadOnly = true;
				resultLock.notify();
			}
		}
	}
	
	public Object getResult(long time) {
		synchronized( resultLock ){
			if( ! isResultReady && !isResultReadOnly ){
				if( time > 0 ){
					try{
						resultLock.wait(time);
					} catch( InterruptedException e){}
					cancelledTime = System.currentTimeMillis();
				}
			}
			isResultReadOnly = true;
		}
		return result;
		
	}
	
	/**
	 * callback����
	 * ����������������г�ʱʱ����.
	 *
	 */
	public void waitTimeout(){
		setResult( null );
		this.status = -1;
		this.reason = "�Ŷӳ�ʱ";
		this.time_outwaitqueue();
	}
	public void serverDown(){
		setResult(null);
		this.status = -4;
		this.reason = "������������";
	}
	public void illegalRequest(){
		setResult(null );
		this.status = -3;
		this.reason = "�Ƿ�����";
	}
	public void connectTimeout(){
		setResult(null);
		this.status = -5;
		this.reason = "���ӳ�ʱ";
		this.time_connect_end();
	}
	public void socketTimeout(){
		setResult(null);
		this.status = -2;
		this.reason = "socket��ʱ";
		this.timeIoend();
	}
	public void decodeFailed(){
		setResult(null);
		this.status = -6;
		this.reason = "����ʧ��";
		this.timeIoend();
	}
	public void poolDown(){
		setResult(null);
		this.status = -7;
		this.reason = "���ӳ�״̬�쳣";
	}

	
	public long getEndTime() {
		return time_end;
	}

	public WeakReference getRef() {
		return ref;
	}
	public void setRef(WeakReference ref) {
		this.ref = ref;
	}
	public long getStartTime() {
		return time_start;
	}

	public String getServerInfo() {
		return serverInfo;
	}

	public final long getTime() {
		long ret = getIoTime(Long.MIN_VALUE);
		if( ret == Long.MIN_VALUE ){
			ret = userWaitTime();
		}
		return ret;
	}

	public void setRequestId(long id) {
		this.requestId = id;
	}
	
	public long getRequestId(){
		return this.requestId;
	}

	public void setServerInfo(String info) {
		this.serverInfo = info;
	}

	public final void setTime(long t) {
		
	}
	public abstract boolean isValid();
	public abstract int getServerId(int total);
	public long getCancelledTime() {
		return cancelledTime;
	}
	public void setCancelledTime(long cancelledTime) {
		this.cancelledTime = cancelledTime;
	}
	public long getConnectEndTime() {
		return time_connect_end;
	}
	public long getEndDumpTime() {
		return endDumpTime;
	}
	public long getRequestTime() {
		return time_request;
	}
	
	public String dumpTimeStatus(){
		return ( "status:" + status + ", id:" + this.requestId + ", start:"+ time_start
				+ ", wait_q:" + ((time_waitqueue==0)?"NUL":String.valueOf(time_waitqueue-time_start))
				+ ", wait_q_out:" + ((time_outwaitqueue==0)?"NUL":String.valueOf(time_outwaitqueue-time_start))
				+ ", reg_q:" + ((time_enqueue==0)?"NUL":String.valueOf(time_enqueue-time_start))
				+ ", reg_q_end:" + ((time_enqueue_end==0)?"NUL":String.valueOf(time_enqueue_end-time_start))
				+ ", reg_q_out:" + ((time_outqueue==0)?"NUL":String.valueOf(time_outqueue-time_start))
				+ ", req:" +  ((time_request==0)?"NUL":String.valueOf(time_request-time_start))
				+ ", ioend:" + getIoTime()
				+ ", conn:" + ((time_connect==0)?"NUL":String.valueOf(time_connect-time_start))
				+ ", conn_end:" + ((time_connect_end==0)?"NUL":String.valueOf(time_connect_end-time_start))
				+ ", end:" +  ((time_end==0)?"NUL":String.valueOf(time_end-time_start))
				+ ", userEnd:" + ((cancelledTime==0)?"NUL":String.valueOf(cancelledTime - time_start))
				+ ", reason:" + reason
				+ ", serverInfo" + this.getServerInfo()
				+ ", start:" + time_start
				+ ", wait_q:" + time_waitqueue
				+ ", wait_q_out:" + time_outwaitqueue
				+ ", reg_q:" + time_enqueue
				+ ", reg_q_end:" + time_enqueue_end
				+ ", reg_q_out:" + time_outqueue
				+ ", try_send:" + time_trySend
				+ ", req:" + time_request
				+ ", last_in:" + time_handleInput
				+ ", ioend:" + time_ioend
				+ ", conn:" + time_connect
				+ ", conn_end:" + time_connect_end
				+ ", cancelTime:" + cancelledTime
				);

	}
	public UdpServerStatus getServer() {
		return server;
	}
	public void setServer(UdpServerStatus server) {
		this.server = server;
	}
	public void timeIoend(){
		this.time_ioend = System.currentTimeMillis();
	}
	public long getIoTime(){
		return getIoTime(0);
	}
	public long getIoTime(long defaultValue){
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
	public long getConnectTime(){
		return ((time_connect_end==0||time_connect==0)?0:(time_connect_end-time_connect));
	}

	public void time_connect() {
		this.time_connect = System.currentTimeMillis();
	}

	public void time_enqueue() {
		this.time_enqueue = System.currentTimeMillis();
	}

	public void time_enqueue_end() {
		this.time_enqueue_end = System.currentTimeMillis();
	}

	public void time_outqueue() {
		this.time_outqueue =System.currentTimeMillis();
	}
	public void time_connect_end(){
		this.time_connect_end = System.currentTimeMillis();
	}
	public void time_waitqueue() {
		this.time_waitqueue =System.currentTimeMillis();
	}
	public void time_outwaitqueue(){
		this.time_outwaitqueue = System.currentTimeMillis();
	}
	public void setQueueTime(long t ){
		
	}
	public long getQueueTime(){
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
	public long userWaitTime(){
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the time_handleInput
	 */
	public Long getTime_handleInput() {
		return time_handleInput;
	}
	/**
	 * @param time_handleInput the time_handleInput to set
	 */
	public void time_handleInput() {
		this.time_handleInput = System.currentTimeMillis();
	}
	/**
	 * @return the time_trySend
	 */
	public Long getTime_trySend() {
		return time_trySend;
	}
	/**
	 * @param time_trySend the time_trySend to set
	 */
	public void time_trySend() {
		this.time_trySend = System.currentTimeMillis();
	}
	/**
	 * @return the identityObj
	 */
	public Object getIdentityObj() {
		return identityObj;
	}
	/**
	 * @param identityObj the identityObj to set
	 */
	public void setIdentityObj(Object identityObj) {
		this.identityObj = identityObj;
	}
	/**
	 * @return the serverId
	 */
	public int getServerId() {
		return serverId;
	}
	/**
	 * @param serverId the serverId to set
	 */
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	/**
	 * @return the isProbe
	 */
	public boolean isProbe() {
		return isProbe;
	}
	/**
	 * @param isProbe the isProbe to set
	 */
	public void setProbe(boolean isProbe) {
		this.isProbe = isProbe;
	}
	/**
	 * @dinghui
	 */
	int ConnectionErrorStatus=0;
	
	public void setConnectionErrorStatus(int ConnectionErrorStatus){
		this.ConnectionErrorStatus=ConnectionErrorStatus;
	}
	
	public int getConnectionErrorStatus(){
		return this.ConnectionErrorStatus;
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
