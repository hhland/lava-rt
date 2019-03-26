/**
 * ���ڽ��շ���������Ӧ����. ��鳬ʱ��ѯ, ע����channel, �����������.
 * @author liumingzhu
 */
package lava.rt.pool.impl;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;


class UdpReceiver implements Runnable{
	
	private static final Log logger = LogFactory.getLog( UdpReceiver.class);

	String generation = "(RecverErr)";
	volatile Thread _thread;
	private LinkedList selectionKeyQueue = new LinkedList();
	UdpGenericConnectionPool pool ;
	
	private static int GENERATION = 0;
	private static Object GENERATION_LOCK = new Object();
	private static int newGeneration(){
		int ret = 0;
		synchronized( GENERATION_LOCK ){
			ret = ++ GENERATION;
		}
		return ret;
	}
	
	UdpReceiver(UdpGenericConnectionPool p){
		this.pool = p;
		this.generation = "(RecverErr)";
	}

	void queueChannel(UdpGenericQueryClient obj){
		synchronized( selectionKeyQueue){
			selectionKeyQueue.offer( obj );
		}
		if( logger.isTraceEnabled() ){
			logger.trace(this.generation + "Add one Client to Busyqueue");
		}
	}
	
	public void startThread(){
		this.generation = this.pool.serverConfig.name + "(Recver" + newGeneration() + ")";
		_thread = new Thread(this, this.generation );
		_thread.start();
	}
	public void stopThread(){
		_thread = null;
	}

	private void registerNewChannel(){
		long start = System.currentTimeMillis();
		if( logger.isTraceEnabled() ){
			logger.trace(this.generation+"Register:" + start );
		}
		
		synchronized( selectionKeyQueue ){
			do {

				UdpGenericQueryClient sc = (UdpGenericQueryClient)selectionKeyQueue.poll();
				
				if( sc == null ) break;
				
				UdpRequest request = sc.getRequest();
				
				if( request == null ) break;
				
				request.time_outqueue();
				
				int option;
				option = SelectionKey.OP_READ;

				boolean needReturnClient = true;
				try {
					DatagramChannel channel = sc.getChannel();
					if( channel != null ){
						channel.register(pool.selector, option, sc);
						needReturnClient = false;
					}
				} catch (ClosedChannelException e) {
					/**
					 * �����Ѿ���ʱ
					 */
					request.serverDown();
					sc.serverStatus.sendError();
					if (logger.isWarnEnabled()) {
						logger.warn(this.generation
								+ "Register:Closed SocketChannel!", e);
					}

				} catch (IllegalBlockingModeException e) {
					// Should Never Happen!
					if (logger.isWarnEnabled()) {
						logger.warn(this.generation
								+ "Register:IllegalBlocking SocketChannel!", e);
					}
					// ����
				} catch (IllegalSelectorException e) {
					// Should Never Happen
					if (logger.isWarnEnabled()) {
						logger.warn(this.generation
								+ "Register:IllegalSelector Why?", e);
					}
					// ����
				} catch (CancelledKeyException e) {
					// Should Never Happen
					if (logger.isWarnEnabled()) {
						logger.warn(this.generation
								+ "Register:Cancelled SocketChannel", e);
					}
					// ����
				} catch (IllegalArgumentException e) {
					// Should Never Happen
					if (logger.isWarnEnabled()) {
						logger.warn(this.generation
								+ "Register:PLEASE CHECK CODE!", e);
					}
					// ����
				}
				if( needReturnClient ){
					sc.serverStatus.freeClient(sc);
				}
			} while (true);
		}
		long end = System.currentTimeMillis();
		if( logger.isTraceEnabled() ){
			logger.trace(this.generation+"Register:" + (end-start) );
		}

	}
	
	
	private void checkSocketTimeoutChannel(){
		long start = System.currentTimeMillis();
		if( logger.isTraceEnabled() ){
			logger.trace(this.generation+"CheckTimeout:" + start );
		}
		UdpServerStatus[] sss = this.pool.getAllStatus();
		if( sss != null ){
			for( int i =0; i<sss.length; i++ ){
				long tstart = System.currentTimeMillis();
				if( logger.isTraceEnabled() ){
					logger.trace(this.generation+"CheckTimeout:Server:"+ i +"At:" + tstart );
				}
				sss[i].checkTimeout();
				if( logger.isTraceEnabled() ){
					logger.trace(this.generation+"CheckTimeout:Server:"+ i +"Time:" + (System.currentTimeMillis()-tstart) );
				}
			}
		}
		long end = System.currentTimeMillis();
		if( logger.isTraceEnabled() ){
			logger.trace(this.generation+"CheckTimeoutEnd:" + (end-start) );
		}
	}

	/**
	 * ��ʱ������cancel�����.
	 */
	public void run(){
		while(_thread == Thread.currentThread() ){
			try{
				long cycleStart = System.currentTimeMillis();
				if( logger.isDebugEnabled() ){
					logger.debug(this.generation+"CycleStart:" + cycleStart);
				}
	//			Thread.yield();
				registerNewChannel();
				checkSocketTimeoutChannel();
				if( logger.isTraceEnabled() ){
					logger.trace(this.generation+"SelectAt:" + (System.currentTimeMillis()-cycleStart) + ",robin:" + pool.serverConfig.robinTime);
				}
				int num = pool.selector.select(pool.serverConfig.robinTime);
				if( logger.isDebugEnabled() ){
					logger.debug(this.generation+"SelectEnd,KeyNum( num = )" + num + ",At:" + (System.currentTimeMillis()-cycleStart) );
				}
				// caused by timeout
				if( num == 0 ){
					continue;
				}
	
				Set set = pool.selector.selectedKeys();
				Iterator it = set.iterator();
				while( it.hasNext() ){
					SelectionKey key = (SelectionKey)it.next();
					it.remove();
					try{
						if( key.isReadable() ){
							if( logger.isTraceEnabled() ){
								logger.trace(this.generation+"ReadKey:" + (System.currentTimeMillis()-cycleStart) );
							}
							UdpGenericQueryClient conn = (UdpGenericQueryClient )key.attachment();
							
							UdpRequest request = conn.getRequest();
							
							try{
								if( request != null ){
									request.time_handleInput();
								}
								int status = conn.handleInput();
								if( status > 0 ){
									if( conn.finishResponse() ){
										if( logger.isTraceEnabled() ){
											logger.trace(this.generation + "Handle InPut End");
										}
										conn.serverStatus.success();
										conn.reset();
										conn.serverStatus.freeClient(conn);
									}
								} else if( status < 0 ) {
									
										// socket Closed by Server
									if( request != null ){
										request.serverDown();
									}
									if( conn.requestSent() ){
										conn.serverStatus.sendError();
									}
		
									if( logger.isTraceEnabled() ){
										logger.trace(this.generation + "Handle InPut End, Server Close");
									}
									conn.reset();
									conn.close();
									conn.serverStatus.freeClient(conn);
								}
							} catch ( Exception e){
								key.cancel();
								conn.serverStatus.sendError();
								if( request != null ){
									request.serverDown();
								}
								conn.close();
								conn.serverStatus.freeClient(conn);
								if(logger.isWarnEnabled() ){
									logger.warn(this.generation + "IOE while Handle Input",e);
								}
							}
							if( logger.isTraceEnabled() ){
								logger.trace(this.generation+"ReadKeyEnd:" + (System.currentTimeMillis()-cycleStart) );
							}
						}
					}catch( CancelledKeyException e ){
						// ignore
						if( logger.isTraceEnabled() ){
							logger.trace(this.generation + "CancelledKey!");
						}
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}catch( ClosedSelectorException e){
				e.printStackTrace();
			}catch( IllegalArgumentException e){
				// shall never happen
				e.printStackTrace();
			} catch( Throwable e){
				e.printStackTrace();
			}
		}
	}
}
