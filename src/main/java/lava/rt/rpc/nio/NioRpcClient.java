package lava.rt.rpc.nio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import lava.rt.rpc.RpcClient;

/**
 * 它所做的工作是利用代理封装方法
 * 
 * @author Administrator 问题是如何获取SocketChannel进行发送和接收
 */
public class NioRpcClient extends RpcClient {
	private final SocketChannel channel;
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	
	private Selector selector = null;
	
	

	public NioRpcClient(String hostname ,int port) throws IOException {
		super(new InetSocketAddress(hostname, port));
		selector = Selector.open();
		channel = SocketChannel.open(addr);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
	}
	
	public NioRpcClient(InetSocketAddress addr) throws IOException {
		super(addr);
		selector = Selector.open();
		channel = SocketChannel.open(addr);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
	}
	

	

	

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(Class<T> clazz) throws Exception {
//动态产生实现类

		T ret=(T)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clazz }, 
				 new ProxyHandler(clazz)
				);

		return ret;
	}

	

	

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		selector.close();
		channel.close();
	}

	

}
