
package lava.rt.pool.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

import lava.rt.aio.ConnectionPool;
import lava.rt.logging.Log;




public abstract class AsyncGenericConnectionPool extends ConnectionPool {

    // / random
    protected static final Random random                   = new Random();

    
    protected AsyncServerStatus[]      status;
   
    protected int                 inplaceConnectionLife    = 500;

    Selector                      selector;

    protected AsyncReceiver            recver;
    protected AsyncSender              sender;
    protected AsyncChecker             checker;

    protected Object              recverLock               = new Object();
    protected Object              senderLock               = new Object();

    protected AsyncClientFactory  factory;

    protected ServerConfig serverConfig=new ServerConfig();
    
    private boolean               isAutoSwitchToNextServer = true;

    
    protected AsyncGenericConnectionPool(AsyncClientFactory factory,ServerConfig serverConfig) {
        this.factory = factory;
        this.serverConfig=serverConfig;
        
        
    }
   
    protected AsyncGenericConnectionPool(AsyncClientFactory factory, String name) {
        this.factory = factory;
        
        this.serverConfig.name = name;
       
    }

    public void init() throws Exception {

        ArrayList servers = new ArrayList();

        if (this.serverConfig.servers == null)
            throw new IllegalArgumentException("config is NULL");

        String[] list = pat.split(this.serverConfig.servers);

        for (int i = 0; i < list.length; i++) {
            AsyncServerStatus ss = new AsyncServerStatus(list[i], this);
            servers.add(servers.size(), ss);
        }

        AsyncServerStatus[] serverStatus = (AsyncServerStatus[]) servers.toArray(new AsyncServerStatus[servers.size()]);

        selector = Selector.open();

        this.status = serverStatus;

        recver = new AsyncReceiver(this);
        sender = new AsyncSender(this);

        recver.startThread();
        sender.startThread();

        if (this.serverConfig.maxResponseTime > 0) {
            checker = new AsyncChecker(this);
            checker.startThread();
        }
    }

    /**
     * ��ü�¼��ʵ��
     * 
     * @return
     */
    protected abstract Log getLogger();

    public int sendRequest(AsyncRequest request) {

        if (request == null) {
            return -1;
        }

        if (!request.isValid()) {
            request.illegalRequest();
            return -2;
        }

        int serverCount = this.getServerIdCount();
        int ret = request.getServerId(serverCount);

        if (!isServerAvaliable(ret) && request.clonableRequest && request.connectType == AsyncRequest.NORMAL_REQUEST
                && isServerShouldRerty(ret)) {
            // debug bart
            System.out.println("[pool " + request.ruid + "]Retry server " + getStatus(ret).serverInfo);
            
            AsyncRequest req = request.clone();
            req.connectType = AsyncRequest.RETRY_REQUEST;
            AsyncServerStatus ss = getStatus(ret);
            if (ss != null) {
                ss.retryCount++;
            }
            sendRequest(req);
        }

        
        if (!isServerAvaliable(ret) && request.connectType == AsyncRequest.NORMAL_REQUEST && !isAutoSwitchToNextServer) {
            request.serverDown("No server available, and no alternatives will be picked");
            return -1;
        }

        
        if (request.connectType == AsyncRequest.SHADOW_NORMAL_REQUEST
                || request.connectType == AsyncRequest.SHADOW_QUEUE_REQUEST ||
                (!isServerAvaliable(ret) && request.connectType != AsyncRequest.RETRY_REQUEST)) {

            // System.out.println("server is not avaliable");
            int avaliableServerCount = 0;
            for (int i = 0; i < getServerIdCount(); i++) {
                if (isServerAvaliable(i)) {
                    avaliableServerCount++;
                }
            }

            // ����Ӱ�����󣬲��ܷ��͸�����
            if ((request.connectType == AsyncRequest.SHADOW_NORMAL_REQUEST || request.connectType == AsyncRequest.SHADOW_QUEUE_REQUEST)
                    && isServerAvaliable(ret)) {
                avaliableServerCount--;
            }

            if (avaliableServerCount <= 0) {
                request.serverDown("��ǰ�޿���server");
                return -1;
            }

            // ���Դ���.
            int inc = (request.getServerId(avaliableServerCount)) + 1;

            int finalIndex = ret;

            int i = 0;
            do {
                int j = 0;
                boolean find = false;
                do {
                    finalIndex = (finalIndex + 1) % serverCount;
                    if (isServerAvaliable(finalIndex) && (finalIndex != ret)) {
                        find = true;
                        break;
                    }
                    j++;
                } while (j < serverCount);

                if (!find) {
                    request.serverDown("����޿���server");
                    return -1;
                }

                i++;
            } while (i < inc);

            ret = finalIndex;
        }

        int serverId = ret;

        if (serverId < 0 || serverId >= this.getServerIdCount()) {
            request.serverDown("ServerId�������");
            return -1;
        }

        AsyncServerStatus ss = getStatus(serverId);

        if (ss == null) {
            request.serverDown("���ܻ�ȡserver״̬");
            return -2;
        }

        request.setServer(ss);
        request.setServerInfo(ss.getServerInfo());
        request.queueSend();
        sender.senderSendRequest(request);

        return 0;
    }

    /**
     * ��ĳ̨server��ʱ���𣬲������䷢������
     * ���߽�ĳ̨server���¼���
     * 
     * @param ip
     *            :������ip���ַ, action:�������
     * @return �ɹ����
     */
    public boolean holdServer(String ip, boolean action) {
        String addr = null;
        int i = 0;
        for (; i < getServerIdCount(); i++) {
            addr = status[i].addr.toString();
            if (addr.indexOf(ip) >= 0) {
                break;
            }
        }
        return holdServer(i, action);
    }

    /**
     * ��ĳ̨server��ʱ���𣬲������䷢������
     * ���߽�ĳ̨server���¼���
     * 
     * @param key
     *            :��������key, action:�������
     * @return �ɹ����
     */
    public boolean holdServer(int key, boolean action) {
        AsyncServerStatus ss = null;
        if (status != null && key >= 0 && key < status.length) {
            ss = status[key];
        }
        StringBuffer sb = new StringBuffer();
        
        if (ss == null) {
        	sb.append("swithed nothing");
        	System.out.println(sb.toString());
            return false;
        }

        ss.swithcer = action;
        
        
        try{
			sb.append(ss.addr.toString());
			sb.append(" is switched ");
			sb.append(action?"on":"off");
			sb.append(" at ");
			sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }catch(Exception e){
        	sb.append("swithed error");
        }
        System.out.println(sb.toString());

		return true;
    }

    /**
     * @return
     */
    public int getServerIdBits() {
        return 0;
    }

    /**
     * @return
     */
    public int getServerIdMask() {
        return 0;
    }

    /**
     * @param i
     * @return ��į������
     */
    public InetSocketAddress getServer(int i) {
        return status[i].getAddr();
    }

    /**
     * @return Returns the inplaceConnectionLife.
     */
    public int getInplaceConnectionLife() {
        return inplaceConnectionLife;
    }

    /**
     * @param inplaceConnectionLife
     *            The inplaceConnectionLife to set.
     */
    public void setInplaceConnectionLife(int inplaceConnectionLife) {
        this.inplaceConnectionLife = inplaceConnectionLife;
    }

    private static Pattern pat = Pattern.compile("\\s+");

    public AsyncServerStatus[] getAllStatus() {
        return status;
    }

    /**
     * ����ָ����ŵķ�������״̬����.
     * 
     * @param i
     * @return ���ָ����ŵķ�����������,�򷵻�null
     */
    public AsyncServerStatus getStatus(int i) {
        if (status != null
                && i >= 0
                && i < status.length) {
            return status[i];
        }
        else {
            return null;
        }
    }

    public boolean isServerShouldRerty(int i) {
        AsyncServerStatus ss = null;
        if (status != null && i >= 0 && i < status.length) {
            ss = status[i];
        }
        if (ss == null) {
            return false;
        }

        return ss.isServerShouldRerty();
    }

    public boolean isServerAvaliable(int i) {
        AsyncServerStatus ss = null;
        if (status != null && i >= 0 && i < status.length) {
            ss = status[i];
        }
        if (ss == null) {
            return false;
        }

        boolean ret = ss.isServerAvaliable();
        if (!ret) {
            Log logger = getLogger();
            if (logger != null )
                logger.info("server is not avaliable:" + ss.getServerInfo());
        }
        return ret;
    }

    /**
     * �������i��Ӧ�ķ����������ӳ��ж�Ӧ�ļ�ֵ.
     * �����Ӧ�ķ������Ƿ�(������),�򷵻�null;
     * 
     * @param i
     * @return
     */
    public Object getServerKey(int i) {
        if (status != null
                && i >= 0
                && i < status.length
                && status[i] != null
                && status[i].key != null) {
            return status[i].key;
        }
        else {
            return null;
        }
    }

    /**
     * ������ע���Ŀ�����������
     * 
     * @return
     */
    public int getServerIdCount() {
        if (status == null) {
            return 0;
        }
        else {
            return status.length;
        }
    }

    public InetSocketAddress getSocketAddress(int i) {
        if (status == null
                || i < 0
                || i >= status.length
                || status[i] == null) {
            return null;
        }
        else {
            return status[i].getAddr();
        }
    }

    public void finalize() {
        destroy();
    }

    public void destroy() {
        sender.stopThread();
        sender = null;
        recver.stopThread();
        recver = null;
        AsyncServerStatus[] temp = status;
        status = null;
        if (temp != null) {
            for (int i = 0; i < temp.length; i++) {
                AsyncServerStatus ss = temp[i];
                if (ss == null)
                    continue;
                ss.destroy();
            }
        }
        try {
            this.selector.close();
        }
        catch (IOException e) {

        }
    }

    public String status() {
        StringBuffer sb = new StringBuffer();
        sb.append("\nPool Status: ");
        sb.append(this.serverConfig.getName());
        sb.append('\n');

        for (int i = 0; i < this.status.length; i++) {
            status[i].status(sb);
        }

       
         getLogger().info(sb.toString());
        
        return sb.toString();
    }

    protected boolean getIsAutoSwitchToNextServer() {
        return this.isAutoSwitchToNextServer;
    }

    protected void setAutoSwitchToNextServer(boolean isAutoSwitchToNextServer) {
        this.isAutoSwitchToNextServer = isAutoSwitchToNextServer;
    }
    
    
    
    
    
    public ServerConfig getServerConfig() {
		return serverConfig;
	}





	public class ServerConfig {


    	/// �������ٴδ����,����sleep����
    	protected int maxErrorsBeforeSleep = 4;
    	///	��������󣬶೤ʱ���ڲ������³���
    	protected int sleepMillisecondsAfterTimeOutError = 30000;
    	protected int maxConnectionsPerServer = 8;
    	protected long connectTimeout = 70;
    	protected long socketTimeout = 10000l;
    	//socketFailTimeoutֻ�д��ڴ�ֵʱ��������Ż�����ʧ�ܣ��Ӷ�����null
    	//һ�������socketFailTimeout = socketTimeout
    	//����ֻҪ��ʱ��fail
    	protected int maxClonedRequest = 2;

    	protected long socketFailTimeout = 0l; 

    	//�Ŷ�ת��ʱ��
    	protected long queueShortTimeout = 600l;
    	//�Ŷӳ�ʱʱ��
    	protected long queueTimeout = 3000l;
    	protected long robinTime = 500;
    	protected int maxQueueSize = 10000;

    	//ƽ����Ӧʱ�����
    	protected long maxResponseTime = 0l;
    	protected int maxResponseRadio = 5;

    	//Ӱ����������
    	protected long shortRetryTime = 0l;

    	public long getShortRetryTime() {
    		return shortRetryTime;
    	}

    	public void setShortRetryTime(long shortRetryTime) {
    		this.shortRetryTime = shortRetryTime;
    	}

    	public long getMaxResponseTime() {
    		return maxResponseTime;
    	}

    	public void setMaxResponseTime(long maxResponseTime) {
    		this.maxResponseTime = maxResponseTime;
    	}

    	public int getMaxResponseRadio() {
    		return maxResponseRadio;
    	}

    	public void setMaxResponseRadio(int maxResponseRadio) {
    		this.maxResponseRadio = maxResponseRadio;
    	}




    	String servers;
    	String name = "Pool";

    	public String getServers() {
    		return servers;
    	}

    	public void setServers(String servers) {
    		this.servers = servers;
    	}

    	public int getMaxClonedRequest() {
    		return maxClonedRequest;
    	}

    	public void setMaxClonedRequest(int maxClonedRequest) {
    		this.maxClonedRequest = maxClonedRequest;
    	}

    	public int getMaxErrorsBeforeSleep() {
    		return maxErrorsBeforeSleep;
    	}

    	public void setMaxErrorsBeforeSleep(int maxErrorsBeforeSleep) {
    		this.maxErrorsBeforeSleep = maxErrorsBeforeSleep;
    	}

    	public int getSleepMillisecondsAfterTimeOutError() {
    		return sleepMillisecondsAfterTimeOutError;
    	}

    	public synchronized void setSleepMillisecondsAfterTimeOutError(
    			int sleepMillisecondsAfterTimeOutError) {
    		this.sleepMillisecondsAfterTimeOutError = sleepMillisecondsAfterTimeOutError;
    	}

    	public int getMaxConnectionsPerServer() {
    		return maxConnectionsPerServer;
    	}

    	public void setMaxConnectionsPerServer(int maxConnectionsPerServer) {
    		this.maxConnectionsPerServer = maxConnectionsPerServer;
    	}

    	public long getConnectTimeout() {
    		return connectTimeout;
    	}

    	public void setConnectTimeout(long connectTimeout) {
    		this.connectTimeout = connectTimeout;
    	}

    	public long getQueueShortTimeout() {
    		if (queueShortTimeout == 0l){
    			return getQueueTimeout();
    		}
    		return queueShortTimeout;
    	}

    	public void setQueueShortTimeout(long queueShortTimeout) {
    		this.queueShortTimeout = queueShortTimeout;
    	}

    	public long getSocketTimeout() {
    		return socketTimeout;
    	}

    	public void setSocketTimeout(long socketTimeout) {
    		this.socketTimeout = socketTimeout;
    	}

    	public long getSocketFailTimeout() {
    		if (socketFailTimeout == 0l){
    			return getSocketTimeout();
    		}
    		return socketFailTimeout;
    	}

    	public void setSocketFailTimeout(long socketFailTimeout) {
    		this.socketFailTimeout = socketFailTimeout;
    	}

    	public String getName() {
    		return name;
    	}

    	public void setName(String name) {
    		this.name = name;
    	}

    	public long getQueueTimeout() {
    		return queueTimeout;
    	}

    	public void setQueueTimeout(long queueTimeout) {
    		this.queueTimeout = queueTimeout;
    	}

    	public long getRobinTime() {
    		return robinTime;
    	}

    	public void setRobinTime(long robinTime) {
    		this.robinTime = robinTime;
    	}

    	/**
    	 * @return the maxQueueSize
    	 */
    	public int getMaxQueueSize() {
    		return maxQueueSize;
    	}

    	/**
    	 * @param maxQueueSize the maxQueueSize to set
    	 */
    	public void setMaxQueueSize(int maxQueueSize) {
    		this.maxQueueSize = maxQueueSize;
    	}

    	}

}
