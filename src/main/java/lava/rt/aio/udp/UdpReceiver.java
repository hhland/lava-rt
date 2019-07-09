/**
 * ���ڽ��շ���������Ӧ����. ��鳬ʱ��ѯ, ע����channel, �����������.
 * @author liumingzhu
 */
package lava.rt.aio.udp;

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

import lava.rt.aio.Receiver;
import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;


class UdpReceiver extends Receiver<UdpGenericQueryClient>{
	
	private static final Log logger = LogFactory.SYSTEM.getLog( UdpReceiver.class);

	
	volatile Thread _thread;
	
	UdpGenericConnectionPool pool ;
	
	
	
	UdpReceiver(UdpGenericConnectionPool p){
		this.pool = p;
		
	}

	
	
	

	private void registerNewChannel(){
		long start = System.currentTimeMillis();
		
			logger.info(this.generation+"Register:" + start );
		
		
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
						//channel.register(pool.selector, option, sc);
						pool.register(channel, option, sc);
						needReturnClient = false;
					}
				} catch (ClosedChannelException e) {
					/**
					 * �����Ѿ���ʱ
					 */
					request.serverDown();
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
		
		UdpServerStatus[] sss = this.pool.getAllStatus();
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
				
					logger.info(this.generation+"SelectAt:" + (System.currentTimeMillis()-cycleStart) + ",robin:" + pool.serverConfig.robinTime);
				
				int num = pool.select(pool.serverConfig.robinTime);
				
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
							
							UdpGenericQueryClient conn = (UdpGenericQueryClient )key.attachment();
							
							UdpRequest request = conn.getRequest();
							
							try{
								if( request != null ){
									request.time_handleInput();
								}
								int status = conn.handleInput();
								if( status > 0 ){
									if( conn.finishResponse() ){
										
											logger.info(this.generation + "Handle InPut End");
										
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
		
									
										logger.info(this.generation + "Handle InPut End, Server Close");
									
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
								
									logger.info(this.generation + "IOE while Handle Input");
								
							}
							
								logger.info(this.generation+"ReadKeyEnd:" + (System.currentTimeMillis()-cycleStart) );
							
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



	@Override
	protected String getServerConfigName() {
		// TODO Auto-generated method stub
		return pool.serverConfig.name;
	}
}
