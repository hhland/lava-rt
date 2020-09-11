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

	

	public abstract <T> T getProxy(final Class<T> serviceInterface) throws Exception;

	

}
