package lava.rt.rpc.aio;



	
	import java.io.Closeable;
	import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lava.rt.logging.Logger;
import lava.rt.logging.LoggerFactory;
import lava.rt.rpc.aio.IMessage.RequestMessage;
import lava.rt.rpc.aio.IMessage.ResponseMessage;
import lava.rt.rpc.aio.IMessage.ResultCode;



	/**
	 * Rpc服务端接口
	 *
	 * @author peiyu
	 */
	public interface IServer extends Closeable {

	    /**
	     * 绑定端口
	     *
	     * @param port 端口
	     * @return RPC服务端接口对象
	     */
	    IServer bind(int port);

	    /**
	     * 服务端工作线程数
	     *
	     * @param threadSize 线程数
	     * @return RPC服务端接口对象
	     */
	    IServer threadSize(int threadSize);

	    /**
	     * 设置超时时间,单位毫秒
	     *
	     * @param timeout 超时时间
	     * @return RPC服务端接口对象
	     */
	    IServer timeout(long timeout);

	    /**
	     * 注册RPC服务
	     *
	     * @param name   服务名称
	     * @param object 服务对象
	     * @return RPC服务端接口对象
	     */
	    IServer register(String name, Object object);


	    IServer register(Object object);

	    /**
	     * 注册RPC服务
	     *
	     * @param serverMap 服务对象Map
	     * @return RPC服务端接口对象
	     */
	    IServer register(Map<String, Object> serverMap);

	    /**
	     * 设置序列化方案,不设置的话,默认使用jdk自带的序列化方案
	     *
	     * @param serializer 序列化方案
	     * @return RPC服务端接口对象
	     */
	  

	    /**
	     * 开始提供服务
	     *
	     * @throws IOException 启动时异常
	     */
	    void start() throws IOException;

		IServer serializer(ISerializer serializer);
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	}

	

