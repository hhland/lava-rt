package lava.rt.aio.tcp;

import java.text.SimpleDateFormat;
import java.util.Date;

class TcpChecker implements Runnable {

	protected TcpGenericConnectionPool pool = null;
	protected Thread _thread = null;
	protected Object _threadLock = new Object();
	
	protected TcpChecker(TcpGenericConnectionPool pool){
		this.pool = pool;
	}
	
	public void startThread(){
		synchronized(_threadLock){
			if( _thread == null || !_thread.isAlive()){
				_thread = new Thread(this, this.pool.getServerConfig().getName()+"(Checker)");
				_thread.start();
			}
		}
	}
	public void stopThread(){
		synchronized(_threadLock){
			_thread = null;
		}
		
	}
	public void run(){
		while(true){
			// check if thread has been stopped
			if( _thread != Thread.currentThread())
				break;
			
			do{ 
				StringBuffer sb = new StringBuffer();
				sb.append("[STATUS] [");
				sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				sb.append("--\t\t\t");
				
				TcpServerStatus[] sss = this.pool.getAllStatus();
				if( sss == null ) break;
				
				int liveServers = 0;
				long totalTimes = 0;
				long maxTime = 0;
				int maxServer = -1;
				boolean hasLongDead = false;
				int hasFreeServer = 0; 
				for (int i = 0; i < sss.length; i++) {
					
					TcpServerStatus ss = sss[i]; 
					ss.statusLog(sb);
					if (ss != null && ss.isServerAlive()){
						if (ss.getServerAvgTime() > 0){
							if (ss.longRequestDead)
								hasLongDead = true; 
							liveServers ++;
							totalTimes +=  ss.getServerAvgTime();
							if (maxTime < ss.getServerAvgTime()){
								maxTime = ss.getServerAvgTime();
								maxServer = i;
							}
						}
						
						if (ss.waitQueue.size() < 2){
							hasFreeServer ++;
						}
					}
				}
				sb.append("]");
				System.err.println(sb.toString());
				long avgTime = 0;
				if (liveServers > 1){
					avgTime = (totalTimes-maxTime)/(liveServers-1);
				}
				
				//���ݵ�ǰ�Ƿ��п���server�������з������Ŀ�ת��״̬
				//������з��������ж��Ѿ����ˣ���Ӧ��ֹͣת��
				boolean shouldClone = false;
				if (hasFreeServer >= sss.length/3){
					shouldClone = true;
				}else{
					System.out.println("server reach limit!");
				}
                // System.out.println("freeServer num:"+hasFreeServer);
					
				for (int i = 0; i < sss.length; i++) {
					TcpServerStatus ss = sss[i]; 
					if (ss != null){
						ss.shouldCloneFlag = shouldClone;
					}
				}
				
				if (avgTime > 0){
					for (int i = 0; i < sss.length; i++) {
						//���Ŀǰ��Ϊƽ��ʱ������������Ļ���״̬
						TcpServerStatus ss = sss[i]; 
						if (ss != null && ss.longRequestDead){
							if (ss.getServerAvgTime() < pool.getServerConfig().getMaxResponseTime() || ss.getServerAvgTime()/avgTime < pool.getServerConfig().getMaxResponseRadio()){
								//debug bart
								System.out.println("[pool]Server is back from long request dead: "+ss.serverInfo);
								ss.longRequestDead = false;
							}
						}
					}
					if (!hasLongDead && maxServer >= 0 && liveServers == sss.length && liveServers > 2){
						//Ŀǰserver������
						if (maxTime >= pool.getServerConfig().getMaxResponseTime() && maxTime/avgTime >= pool.getServerConfig().getMaxResponseRadio()){
							//���server��Ӧʱ����ƽ����Ӧʱ�������
							//�ߵ���server
							//debug bart
							System.out.println("[pool]Server is dead because long request dead: "+sss[maxServer].serverInfo);
							sss[maxServer].longRequestDead = true;
						}
					}
				}
			} while( false ); // �������ж�do .. whileѭ��
			
			// ִ����һ�����������
			try{
				synchronized(_threadLock){
					_threadLock.wait(1000);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * @return the pool
	 */
	public TcpGenericConnectionPool getPool() {
		return pool;
	}
	/**
	 * @param pool the pool to set
	 */
	public void setPool(TcpGenericConnectionPool pool) {
		this.pool = pool;
	}
}
