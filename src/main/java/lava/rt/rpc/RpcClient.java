package lava.rt.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public abstract class RpcClient {

	private InetSocketAddress addr;

	protected final Map<Class, ProxyHandler> handlerMap = new HashMap<>();

	public RpcClient(InetSocketAddress addr) {
		super();
		this.addr = addr;
	}

	public abstract <T> T getProxy(final Class<T> serviceInterface);

	protected class ProxyHandler implements InvocationHandler {

		final Class serviceInterface;

		public ProxyHandler(Class serviceInterface) {
			super();
			this.serviceInterface = serviceInterface;
			
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// TODO Auto-generated method stub
			Socket socket = null;
			ObjectOutputStream output = null;
			ObjectInputStream input = null;
			try {
				// 2.创建Socket客户端，根据指定地址连接远程服务提供者
				socket = new Socket();
				socket.connect(addr);

				// 3.将远程服务调用所需的接口类、方法名、参数列表等编码后发送给服务提供者
				output = new ObjectOutputStream(socket.getOutputStream());
				output.writeUTF(serviceInterface.getName());
				output.writeUTF(method.getName());
				output.writeObject(method.getParameterTypes());
				output.writeObject(args);

				// 4.同步阻塞等待服务器返回应答，获取应答后返回
				input = new ObjectInputStream(socket.getInputStream());
				return input.readObject();
			} finally {
				if (socket != null)
					socket.close();
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			}
		}

	}

}
