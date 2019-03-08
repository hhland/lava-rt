/*
 * Created on 2003-11-24
 *
 */
package lava.rt.connectionpool;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Mingzhu Liu( mingzhuliu@sohu-inc.com) 
 *
 */
public interface QueryClient {
	public static final int ITEMS_PER_PAGE = 10;//20;
	public static final int MAX_ITEMS_PER_PAGE = 100;
	
	public static final int ITEMS_PER_CACHE = 10;
	
	public static final int MAX_ITEMS = 1000;
	
	/**
	 * �ر�����
	 */
	public abstract void close();
	/**
	 * ��cache�������ύ��ѯ
	 * @param request
	 * @return
	 */
	public abstract Result query(Request request) throws IOException;
	/**
	 * �Ƿ��ѹر�
	 * @return
	 */
	public abstract boolean isValid();
	/**
	 * ���ӷ�����
	 * @param addr
	 * @param connTimeoutMills
	 * @param socketTimeoutMillis
	 */
	public void connect(InetSocketAddress addr, int connectTimeoutMillis, int socketTimeoutMillis) throws IOException;
	
	/**
	 * ���ӵ�����
	 * @param life
	 */
	public void setLife(int life);
	

}