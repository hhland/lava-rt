package lava.rt.rpc;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public abstract class RpcClient implements Closeable {

	
	protected final InetSocketAddress addr;
	
	
	
	

	public RpcClient(InetSocketAddress addr) throws IOException {
		super();
		this.addr = addr;
	}



	public abstract <T> T getService(final Class<T> serviceInterface) throws Exception;

	
	
	protected Object toObject(byte[] bytes) throws IOException {
		// TODO Auto-generated method stub
		Object ret=null;
        
        try(final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);){
            try {
				ret = objectInputStream.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
       
        return ret;
	}
	
	
	
	protected class ProxyHandler implements InvocationHandler {

		final Class serviceInterface;
		
		

		public ProxyHandler(Class serviceInterface) {
			super();
			this.serviceInterface = serviceInterface;
			
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// TODO Auto-generated method stub
			Object ret=null;
			try (
					Socket socket = new Socket(addr.getAddress(),addr.getPort());
					ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
					ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
					){
				
				output.writeUTF(serviceInterface.getName());
				output.writeUTF(method.getName());
				output.writeObject(method.getParameterTypes());
				output.writeObject(args);

				ret=input.readObject();
			} 
			return ret;
		}

	}
	
	

}
