package lava.rt.aio;

import java.util.LinkedList;


public abstract class Receiver<C> implements Runnable {

	protected LinkedList<C> selectionKeyQueue = new LinkedList<>();
	
	volatile protected Thread _thread;
	
	protected String generation = "(RecverErr)";
	
	
	private static int GENERATION = 0;
	private static Object GENERATION_LOCK = new Object();
	protected static int newGeneration(){
		int ret = 0;
		synchronized( GENERATION_LOCK ){
			ret = ++ GENERATION;
		}
		return ret;
	}
	
	
	public void queueChannel(C obj){
		synchronized( selectionKeyQueue){
			selectionKeyQueue.offer( obj );
		}
		
	}

	protected abstract  String getServerConfigName();
	
	
	
	public void startThread(){
		this.generation = getServerConfigName() + "(Recver" + newGeneration() + ")";
		_thread = new Thread(this, this.generation );
		_thread.start();
	}
	public void stopThread(){
		_thread = null;
	}
	
}
