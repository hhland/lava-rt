
package lava.rt.aio.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import lava.rt.aio.ClientFactory;
import lava.rt.aio.ConnectionPool;
import lava.rt.logging.Log;




public abstract class TcpConnectionPool extends ConnectionPool<TcpRequest> {

    // / random
    protected static final Random random                   = new Random();

    
    protected TcpServerStatus[]      status;
   
    protected int                 inplaceConnectionLife    = 500;

    

    protected TcpReceiver            recver;
    protected TcpSender              sender;
    protected TcpChecker             checker;

    protected Object              recverLock               = new Object();
    protected Object              senderLock               = new Object();

    protected ClientFactory<TcpQueryClient>  factory;

    protected TcpServerConfig TcpServerConfig;
    
    private boolean               isAutoSwitchToNextServer = true;

    
    protected TcpConnectionPool(ClientFactory<TcpQueryClient> factory,TcpServerConfig TcpServerConfig) {
        this.factory = factory;
        this.TcpServerConfig=TcpServerConfig;
        
        
    }
   
    

    public void init() throws Exception {

    	super.init();
    	
        List<TcpServerStatus> servers = new ArrayList<>();

        

        String[] list = this.TcpServerConfig.servers;

        for (int i = 0; i < list.length; i++) {
            TcpServerStatus ss = new TcpServerStatus(list[i], this);
            servers.add(servers.size(), ss);
        }

        TcpServerStatus[] serverStatus =  servers.toArray(new TcpServerStatus[servers.size()]);

        

        this.status = serverStatus;

        recver = new TcpReceiver(this);
        sender = new TcpSender(this);

        recver.startThread();
        sender.startThread();

        if (this.TcpServerConfig.maxResponseTime > 0) {
            checker = new TcpChecker(this);
            checker.startThread();
        }
        
    }

   

    public int sendRequest(TcpRequest request) {

        if (request == null) {
            return -1;
        }

        if (!request.isValid()) {
            request.illegalRequest();
            return -2;
        }

        int serverCount = this.getServerIdCount();
        int ret = request.getServerId(serverCount);

        if (!isServerAvaliable(ret) && request.clonableRequest && request.connectType == TcpRequest.NORMAL_REQUEST
                && isServerShouldRerty(ret)) {
            // debug bart
            System.out.println("[pool " + request.ruid + "]Retry server " + getStatus(ret).serverInfo);
            
            TcpRequest req = request.clone();
            req.connectType = TcpRequest.RETRY_REQUEST;
            TcpServerStatus ss = getStatus(ret);
            if (ss != null) {
                ss.retryCount++;
            }
            sendRequest(req);
        }

        
        if (!isServerAvaliable(ret) && request.connectType == TcpRequest.NORMAL_REQUEST && !isAutoSwitchToNextServer) {
            request.serverDown("No server available, and no alternatives will be picked");
            return -1;
        }

        
        if (request.connectType == TcpRequest.SHADOW_NORMAL_REQUEST
                || request.connectType == TcpRequest.SHADOW_QUEUE_REQUEST ||
                (!isServerAvaliable(ret) && request.connectType != TcpRequest.RETRY_REQUEST)) {

            // System.out.println("server is not avaliable");
            int avaliableServerCount = 0;
            for (int i = 0; i < getServerIdCount(); i++) {
                if (isServerAvaliable(i)) {
                    avaliableServerCount++;
                }
            }

            // ����Ӱ�����󣬲��ܷ��͸�����
            if ((request.connectType == TcpRequest.SHADOW_NORMAL_REQUEST || request.connectType == TcpRequest.SHADOW_QUEUE_REQUEST)
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

        TcpServerStatus ss = getStatus(serverId);

        if (ss == null) {
            request.serverDown("���ܻ�ȡserver״̬");
            return -2;
        }

        request.setServer(ss);
        request.setServerInfo(ss.getServerInfo());
        request.queueSend();
        sender.sendRequest(request);

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
        TcpServerStatus ss = null;
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

    public TcpServerStatus[] getAllStatus() {
        return status;
    }

    /**
     * ����ָ����ŵķ�������״̬����.
     * 
     * @param i
     * @return ���ָ����ŵķ�����������,�򷵻�null
     */
    public TcpServerStatus getStatus(int i) {
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
        TcpServerStatus ss = null;
        if (status != null && i >= 0 && i < status.length) {
            ss = status[i];
        }
        if (ss == null) {
            return false;
        }

        return ss.isServerShouldRerty();
    }

    public boolean isServerAvaliable(int i) {
        TcpServerStatus ss = null;
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
        TcpServerStatus[] temp = status;
        status = null;
        if (temp != null) {
            for (int i = 0; i < temp.length; i++) {
                TcpServerStatus ss = temp[i];
                if (ss == null)
                    continue;
                ss.destroy();
            }
        }
        try {
            super.destroy();
        }
        catch (IOException e) {

        }
    }

    public String status() {
        StringBuffer sb = new StringBuffer();
        sb.append("\nPool Status: ");
        sb.append(this.TcpServerConfig.name);
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
    
    
    
    
    
    public TcpServerConfig getServerConfig() {
		return TcpServerConfig;
	}





	

}