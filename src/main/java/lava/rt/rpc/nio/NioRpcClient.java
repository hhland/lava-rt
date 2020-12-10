package lava.rt.rpc.nio;

import java.io.IOException;
import java.net.InetSocketAddress;


public abstract class NioRpcClient<REQ extends NaRPCMessage,RES extends NaRPCMessage>  {

	
	private NaRPCEndpoint<REQ, RES> endpoint;

	public NioRpcClient(NaRPCClientGroup<REQ, RES> clientGroup,InetSocketAddress socketAddress) throws Exception {
		
		// TODO Auto-generated constructor stub
		endpoint=clientGroup.createEndpoint();
		endpoint.connect(socketAddress);
	}

	
    public void invoke(REQ request,RES response) throws IOException {
    	endpoint.issueRequest(request, response);
    	
    }
	

	
	

	
	
}
