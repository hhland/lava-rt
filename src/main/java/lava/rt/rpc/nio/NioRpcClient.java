package lava.rt.rpc.nio;

import java.io.IOException;
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
	
	private final InetSocketAddress addr;

	public NioRpcClient(String hostname ,int port) throws IOException {
		this.addr=new InetSocketAddress(hostname, port);
		selector = Selector.open();
		channel = SocketChannel.open(addr);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
	}
	
	public NioRpcClient(InetSocketAddress addr) throws IOException {
		this.addr=addr;
		selector = Selector.open();
		channel = SocketChannel.open(addr);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
	}
	

	

	

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(Class<T> clazz) throws Exception {
//动态产生实现类

		T ret=(T)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clazz }, new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
// TODO 自动生成的方法存根
				String methodName = method.getName();
				String clazzName = clazz.getName();
				Object result = null;
				if (args == null || args.length == 0) {// 表示没有参数 它传递的类型
// 接口名/方法名()
					channel.write(ByteBuffer.wrap((clazzName + "/" + methodName + "()").getBytes()));
				} else {
					int size = args.length;
					String[] types = new String[size];
					StringBuffer content = new StringBuffer(clazzName).append("/").append(methodName).append("(");
					for (int i = 0; i < size; i++) {
						types[i] = args[i].getClass().getName();
						content.append(types[i]).append(":").append(args[i]);
						if (i != size - 1)
							content.append(",");
					}
					content.append(")");
					channel.write(ByteBuffer.wrap(content.toString().getBytes()));
				}
// 获取结果
				result = getresult();

				return result;
			}
		});

		return ret;
	}

	private Object getresult() {
// 解析结果 如果结尾为null或NULL则忽略
		try {
			while (selector.select() > 0) {
				for (SelectionKey sk : selector.selectedKeys()) {
					selector.selectedKeys().remove(sk);
					if (sk.isReadable()) {
						SocketChannel sc = (SocketChannel) sk.channel();
						buffer.clear();
						sc.read(buffer);
						int postion = buffer.position();

						String result = new String(buffer.array(), 0, postion);
						result = result.trim();
						buffer.clear();

						if (result.endsWith("null") || result.endsWith("NULL"))
							return null;

						String[] typeValue = result.split(":");
						String type = typeValue[0];
						String value = result.substring(type.length()+1);
						if (type.contains("Integer") || type.contains("int"))
							return Integer.parseInt(value);
						else if (type.contains("Float") || type.contains("float"))
							return Float.parseFloat(value);
						else if (type.contains("Long") || type.contains("long"))
							return Long.parseLong(value);
						else
							return value;
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		selector.close();
		channel.close();
	}

	

}
