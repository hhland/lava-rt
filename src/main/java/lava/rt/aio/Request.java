package lava.rt.aio;



public abstract class Request<S> implements Cloneable{

	
	// set by pool, to indicate time status 
		protected volatile Long time_start = Long.valueOf(0l)
		,  time_enqueue = Long.valueOf(0l)
		,time_enqueue_end = Long.valueOf(0l)
		, time_outqueue = Long.valueOf(0l)
		, time_waitqueue = Long.valueOf(0l)
		, time_outwaitqueue = Long.valueOf(0l)
		, time_connect = Long.valueOf(0l)
		, time_connect_end = Long.valueOf(0l) // ���ӽ�����ʱ��( ������Ҫ�����������) 
		, time_request = Long.valueOf(0l) // �����socket���ͳ�ȥ��ʱ��
		,  time_end = Long.valueOf(0l) // ��Ӧ������ȫ��ʱ��
		, time_ioend = Long.valueOf(0l)
		, endDumpTime = Long.valueOf(0l)
		// set by user thread. to indicate time status
		,cancelledTime = Long.valueOf(0l)
		, startLocktime=Long.valueOf(0l)
		, endLocktime=Long.valueOf(0l)
		
		;
	
	
	protected String serverInfo;
	
	protected int status = 0;
	
	
	protected long requestId;


	protected volatile S server;
	
	
	public abstract long getTime();
	
	public abstract int getServerId(int total);
	
	public abstract  void serverDown();
	public abstract void socketTimeout();
	public abstract boolean isValid();
	
	
	public String getServerInfo() {
		return serverInfo;
	}
	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}
	
	
	
	
	public long getRequestId() {
		return requestId;
	}




	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}




	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	
	
	public S getServer() {
		return server;
	}
	public void setServer(S server) {
		this.server = server;
	}
	
	
	
	public final void timeIoend(){
		this.time_ioend = System.currentTimeMillis();
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
	
}