package lava.rt.aio.async;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;




class AsyncReceiver implements Runnable{
	
	private static final Log logger = LogFactory.SYSTEM.getLog( AsyncReceiver.class);

	String generation = "(RecverErr)";
	volatile Thread _thread;
	private LinkedList selectionKeyQueue = new LinkedList();
	AsyncGenericConnectionPool pool ;
	
	private static int GENERATION = 0;
	private static Object GENERATION_LOCK = new Object();
	private static int newGeneration(){
		int ret = 0;
		synchronized( GENERATION_LOCK ){
			ret = ++ GENERATION;
		}
		return ret;
	}
	
	AsyncReceiver(AsyncGenericConnectionPool p){
		this.pool = p;
		this.generation = "(RecverErr)";
	}

	void queueChannel(AsyncGenericQueryClient obj){
		synchronized( selectionKeyQueue){
			selectionKeyQueue.offer( obj );
		}
		
			logger.info(this.generation + "Add one Client to Busyqueue");
		
	}
	
	public void startThread(){
		this.generation = this.pool.getServerConfig().name + "(Recver" + newGeneration() + ")";
		_thread = new Thread(this, this.generation );
		_thread.start();
	}
	public void stopThread(){
		_thread = null;
	}

	/**
	 * ��������
	 * returns 
	 *  -1 �������socket�쳣, ��رո�����, ��֪ͨuser-thread
	 *   0 ����Ƿ�, ֪ͨuser-thread. �黹���ӱ���
	 *  
	 */
	private int sendRequest(AsyncGenericQueryClient sc){
		int status = -1;
		AsyncRequest request = sc.getRequest();
		
		if( request == null ) 
			return 0;
		try{
			status = sc.sendRequest();
		}catch( IOException e){
			
				logger.info(this.generation+"IOE Sending Request");
			
			request.serverDown("[rcv]�������쳣"+e.getMessage());
			sc.serverStatus.sendError();
			sc.close();
		} catch( RuntimeException e){
			
				logger.error(this.generation+"RTE Sending Request");
			
			
		} catch( Exception e){
			
			logger.info(this.generation+"OtherException Sending Request");
			
		}
		request.requestTime();
		// ��¼������ʱ��, �Ա���鳬ʱ
		sc.setTime_request();
		if( status <= 0 ){
			if(status == 0) {
				request.illegalRequest();
			}else {
				request.serverDown("[rcv]����ʧ��:"+status);
			}
			try{
				sc.reset();
			}catch( Exception e){
			
					logger.info(this.generation+"Exception while reseting Request after sendRequest");
				
			}
			sc.serverStatus.freeClient(sc);
		} else {
			sc.requestSent(true);
		}

		return status;

	}
	private void registerNewChannel(){
		long start = System.currentTimeMillis();
		
			logger.info(this.generation+"Register:" + start );
		
		
		synchronized( selectionKeyQueue ){
			do {

				AsyncGenericQueryClient sc = (AsyncGenericQueryClient)selectionKeyQueue.poll();
				
				if( sc == null ) break;
				
				AsyncRequest request = sc.getRequest();
				
				if( request == null ) break;
				
				request.time_outqueue();
				
				int option;
				if( sc.isConnected() ){
					request.time_connect_end();
					if( !sc.requestSent() ){
						if( sendRequest(sc) < 0 ){
							continue;
						}
					}
					option = SelectionKey.OP_READ;
				} else if( sc.isConnectionPending() ){
					option = SelectionKey.OP_CONNECT;
				} else {
					/**
					 *  not connected and no connection pending. 
					 *  This means client did send a request sometime before, but the server
					 *  refused it quickly. this happens when this client is in register_queue.
					 */
					request.serverDown("[reg]�յ�read/conn�������Ϣ");
					sc.close();
					sc.serverStatus.freeClient(sc);
					
						logger.info( this.generation + "QuicklyClosedConnection!");
					
					continue;
				}

				boolean needReturnClient = true;
				try {
					SocketChannel channel = sc.getChannel();
					if( channel != null ){
						//channel.register(pool.selector, option, sc);
						pool.register(channel, option, sc);
						needReturnClient = false;
					}
				} catch (ClosedChannelException e) {
					/**
					 * �����Ѿ���ʱ
					 */
					request.serverDown("[reg]socket���ر�");
					sc.serverStatus.sendError();
					
						logger.info(this.generation
								+ "Register:Closed SocketChannel!");
					

				} catch (IllegalBlockingModeException e) {
					// Should Never Happen!
					
						logger.info(this.generation
								+ "Register:IllegalBlocking SocketChannel!");
					
					// ����
				} catch (IllegalSelectorException e) {
					// Should Never Happen
					
						logger.info(this.generation
								+ "Register:IllegalSelector Why?");
					
					// ����
				} catch (CancelledKeyException e) {
					// Should Never Happen
					
						logger.info(this.generation
								+ "Register:Cancelled SocketChannel");
				
					// ����
				} catch (IllegalArgumentException e) {
					// Should Never Happen
					
						logger.info(this.generation
								+ "Register:PLEASE CHECK CODE!");
					
					// ����
				}
				if( needReturnClient ){
					sc.serverStatus.freeClient(sc);
				}
			} while (true);
		}
		long end = System.currentTimeMillis();
		
			logger.info(this.generation+"Register:" + (end-start) );
		

	}
	
	
	private void checkSocketTimeoutChannel(){
		long start = System.currentTimeMillis();
		
			logger.info(this.generation+"CheckTimeout:" + start );
		
		AsyncServerStatus[] sss = this.pool.getAllStatus();
		if( sss != null ){
			for( int i =0; i<sss.length; i++ ){
				long tstart = System.currentTimeMillis();
				
					logger.info(this.generation+"CheckTimeout:Server:"+ i +"At:" + tstart );
				
				sss[i].checkTimeout();
				
					logger.info(this.generation+"CheckTimeout:Server:"+ i +"Time:" + (System.currentTimeMillis()-tstart) );
				
			}
		}
		long end = System.currentTimeMillis();
		
			logger.info(this.generation+"CheckTimeoutEnd:" + (end-start) );
		
	}

	/**
	 * ��ʱ������cancel�����.
	 */
	public void run(){
		while(_thread == Thread.currentThread() ){
			try{
				long cycleStart = System.currentTimeMillis();
				
					logger.info(this.generation+"CycleStart:" + cycleStart);
				
	//			Thread.yield();
				registerNewChannel();
				checkSocketTimeoutChannel();
				
					logger.info(this.generation+"SelectAt:" + (System.currentTimeMillis()-cycleStart) + ",robin:" + pool.getServerConfig().robinTime);
				
				int num = pool.select(pool.getServerConfig().robinTime);
				
					logger.info(this.generation+"SelectEnd,KeyNum( num = )" + num + ",At:" + (System.currentTimeMillis()-cycleStart) );
				
				// caused by timeout
				if( num == 0 ){
					continue;
				}
	
				Set set = pool.selectedKeys();
				Iterator it = set.iterator();
				while( it.hasNext() ){
					SelectionKey key = (SelectionKey)it.next();
					it.remove();
					try{
						if( key.isReadable() ){
							
								logger.info(this.generation+"ReadKey:" + (System.currentTimeMillis()-cycleStart) );
							
							AsyncGenericQueryClient conn = (AsyncGenericQueryClient )key.attachment();
							
							try{
								int status = conn.handleInput();
								if( status > 0 ){
									if( conn.finishResponse() ){
										//debug bart
										//System.out.println("[pool]finishResponse from "+conn.getRequest().getServerInfo());
										
											logger.info(this.generation + "Handle InPut End");
										
										conn.serverStatus.success();
										conn.reset();
										conn.serverStatus.freeClient(conn);
									}
								} else if( status < 0 ) {
									
										// socket Closed by Server
									AsyncRequest request = conn.getRequest();
									if( request != null ){
										// һ����-1�������ѹ��Է�����������չ��
										// < -50 ��ʾ���Զ���Ĵ����룬ͨ�����Ƿ������˵���Ӧ���Ϸ�
										if( status > -50 ){
											request.serverDown("[rcv]�������ݱ���:"+status);
										} else {
											request.invalidResponse("[rcv]�������ݱ���:"+status);
										}
									}
									if( conn.requestSent() ){
										conn.serverStatus.sendError();
									}
		
									
										logger.info(this.generation + "Handle InPut End, Server Close");
									
									conn.reset();
									conn.close();
									conn.serverStatus.freeClient(conn);
								}
							} catch ( Exception e){
								key.cancel();
								conn.serverStatus.sendError();
								AsyncRequest request = conn.getRequest();
								if( request != null ){
									request.serverDown("δ֪�쳣"+e.getMessage());
								}
								conn.close();
								conn.serverStatus.freeClient(conn);
								
									logger.info(this.generation + "IOE while Handle Input");
								
							}
							
								logger.info(this.generation+"ReadKeyEnd:" + (System.currentTimeMillis()-cycleStart) );
							
						} else if( key.isConnectable() ){
							
								logger.info(this.generation+"ConnKey:" + (System.currentTimeMillis()-cycleStart) );
							
		
							AsyncGenericQueryClient conn = (AsyncGenericQueryClient )key.attachment();
							
							AsyncRequest request = conn.getRequest();
							if( request != null ){
								request.time_connect_end();
							}
							int status = -1;
							
							try{
								if( ((SocketChannel)(key.channel())).finishConnect() ){
									status = 1;
									
										logger.info(this.generation + "finishConnect");
									
								}
							}catch( IOException e){
							
									logger.info(this.generation + ":CONN FAILED: " + e.getMessage() );
								
							}
							
							if( status == 1 ){
								status = sendRequest( conn );
								
									logger.info( this.generation + "Send the Request");
								
								if( status < 0 ){
									
										logger.info( this.generation + "Cancel the key");
									
									key.cancel();
								} else {
									
										logger.info(this.generation + "Change the Status to OPREAD");
									
									key.interestOps(SelectionKey.OP_READ );
								}
							} else {
								
									logger.info(this.generation + "ServerDown");
								
								if( request != null ){
									request.serverDown("����ʧ��");
								}
								conn.serverStatus.connectTimeout();
								conn.serverStatus.freeClient(conn);
							}
							
								logger.info(this.generation+"ConnKeyEnd:" + (System.currentTimeMillis()-cycleStart) );
							
						}
					}catch( CancelledKeyException e ){
						// ignore
						
							logger.info(this.generation + "CancelledKey!");
						
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
