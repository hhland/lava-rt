package lava.rt.aio;



public abstract class Sender<R extends Request> implements Runnable {

	
	protected volatile Thread _thread = null;
	
	protected String generation = "(ThreadErr)"; 
	
	protected long yieldTime = 50;
	protected long minSleepTime = 500;
	protected long maxSleepTime = 1200000l;
	protected long sleepTime;
	
	
	
	private static int GENERATION = 0;
	private static Object GENERATION_LOCK = new Object();
	protected static int newGeneration(){
		int ret = 0;
		synchronized( GENERATION_LOCK ){
			ret = ++ GENERATION;
		}
		return ret;
	}
	
	
	public abstract int sendRequest(R request);
    protected abstract  String getServerConfigName();
	
	
	
	
	public void startThread(){
		sleepTime = minSleepTime;
		this.generation = getServerConfigName()+"(Sender" + newGeneration() + ")";
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
	
}
