/*
 * Created on 2003-11-23
 *
 */
package lava.rt.connectionpool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;



/**
 * ����cache�Ŀͻ���
 * 
 * @author Mingzhu Liu (mingzhuliu@sohu-inc.com)
 *  
 */
public abstract class QueryClientImpl implements QueryClient {

	// ��¼��
	//protected Log logger = getLogger();

	protected Socket socket = null;
	protected BufferedReader reader = null;
	protected OutputStream os = null;
	
	// ���ӵ�����ֵ.����0��ʾ���ɿ�.
	protected int life = 0;
	
	public synchronized void setLife(int lf) {
		this.life = lf;
	}
	
	public synchronized int getLife(){
		return this.life;
	}

	/**
	 * �½�һ���ͻ���
	 */
	public QueryClientImpl() {
	}


	/**
	 * �ر�����
	 */
	public synchronized void close() {
		try {
			if (os != null){
				try{
					os.close();
				}catch(Exception e){
					//logger.debug(this,e);
				}
			}
			if (reader != null){
				try{
					reader.close();
				}catch(Exception e){
					/////logger.debug(this,e);
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
					//logger.debug(this, e);
				}
			}
		} finally {
			if (socket != null) {
				if (!socket.isClosed()) {}
					//logger.debug("EERROR:socket: NOT isClosed");
				if (socket.isConnected()) {}
					//logger.debug("EERROR:socket:isConnected");
			} else {
				//logger.debug("EERROR:socket:==NULL");
			}
			reader = null;
			os = null;
			socket = null;
		}
	}


	public synchronized boolean isValid() {
		if( life <= 0 || socket==null || socket.isClosed() || (!socket.isConnected() ))
			return false;
		else 
			return true;
	}

	public synchronized void connect(InetSocketAddress addr, int connectTimeoutMills, int socketTimeoutMillis) throws IOException {
		Socket tmpSocket = new Socket();
		tmpSocket.connect(addr, connectTimeoutMills);
		tmpSocket.setSoTimeout( socketTimeoutMillis );
		BufferedReader tmpReader = new BufferedReader( new InputStreamReader(tmpSocket.getInputStream() ,"GBK"));
//		PrintWriter tmpWriter = new PrintWriter( new OutputStreamWriter( tmpSocket.getOutputStream(), "GBK"), false );
		OutputStream tmpOs = tmpSocket.getOutputStream();
		
		this.socket = tmpSocket;
		this.reader = tmpReader;
		this.os = tmpOs;
	}
	//protected abstract Log getLogger();
	/**
	 * ��cache�������ύ��ѯ
	 * 
	 * @param request
	 * @return
	 */
	public abstract Result query(Request objRequest) throws IOException ;

}
