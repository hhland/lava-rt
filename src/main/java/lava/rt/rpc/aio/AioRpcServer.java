package lava.rt.rpc.aio;

import java.io.IOException;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


import lava.rt.rpc.RpcException;
import lava.rt.rpc.RpcServer;
import lava.rt.rpc.aio.IMessage.RequestMessage;
import lava.rt.rpc.aio.IMessage.ResponseMessage;
import lava.rt.rpc.aio.IMessage.ResultCode;


import lava.rt.rpc.aio.ISerializer.JdkSerializer;
import lava.rt.wrapper.LoggerWrapper;
import lava.rt.rpc.aio.IChannel.FastChannel;;

public final class AioRpcServer extends RpcServer implements IServer {

    private final LoggerWrapper          log      = LoggerWrapper.CONSOLE;
    private       int             threadSize      = Runtime.getRuntime().availableProcessors() * 2;
    private       ISerializer     serializer      = new JdkSerializer();
    private       long            timeout         = 5000;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10000);

    
    private       AsynchronousChannelGroup        group;
    private       AsynchronousServerSocketChannel channel;
    private final Map<String, Object>             serverMap;

    public AioRpcServer(final int port) {
    	super(port);
        this.serverMap = new ConcurrentHashMap<>();
       
    }

  

    @Override
    public IServer threadSize(final int threadSize) {
        if (0 < threadSize) {
            this.threadSize = threadSize;
        } else {
            //log.warn("threadSize must > 0!");
        }
        return this;
    }

    @Override
    public IServer timeout(final long timeout) {
        if (0 < timeout) {
            this.timeout = timeout;
        } else {
            //log.warn("timeout must > 0");
        }
        return this;
    }

    @Override
    public IServer register(final String name, final Object object) {
        Objects.requireNonNull(name, "server'name is null");
        Objects.requireNonNull(object, "server " + name + " is null");
        this.serverMap.put(name, object);
        return this;
    }

    
    
    
    @Override
	public <I, T extends I> IServer register(Class<I> intfCls, T object) {
		// TODO Auto-generated method stub
    	Objects.requireNonNull(object, "server is null");
        String key=intfCls.getName();
        this.serverMap.put(key, object);
        return this;
	}
    

    @Override
    public IServer register(final Map<String, Object> serverMap) {
        Objects.requireNonNull(serverMap, "serverMap is null");
        serverMap.forEach(this::register);
        return this;
    }

    @Override
    public IServer serializer(final ISerializer serializer) {
        this.serializer = serializer;
        return this;
    }

    @Override
    public void start() throws IOException {
        //log.debug("开始启动RPC服务端......");
        this.group = AsynchronousChannelGroup.withFixedThreadPool(this.threadSize, Executors.defaultThreadFactory());
        this.channel = AsynchronousServerSocketChannel
                .open(this.group)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(addr);

        this.channel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(final AsynchronousSocketChannel result, final Void attachment) {
                channel.accept(null, this);
                String localAddress = null;
                String remoteAddress = null;
                try {
                    localAddress = result.getLocalAddress().toString();
                    remoteAddress = result.getRemoteAddress().toString();
                    log.info("创建连接 {} <-> {}" + localAddress+ remoteAddress);
                } catch (final IOException e) {
                    //log.error("", e);
                }
                final IChannel channel = new FastChannel(result, serializer, timeout);
                while (channel.isOpen()) {
                    handler(channel);
                }
                //log.debug("断开连接 {} <-> {}", localAddress, remoteAddress);
            }

            @Override
            public void failed(final Throwable exc, final Void attachment) {
                log.info("通信失败"+ exc.getMessage());
                try {
                    close();
                } catch (final IOException e) {
                    log.info("关闭通道异常"+ e.getMessage());
                }
            }
        });
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
        this.group.shutdownNow();
    }

    private void handler(final IChannel channel) {
        try {
            final RequestMessage request = channel.read(RequestMessage.class);
            if (Objects.nonNull(request)) {
                final String serverName = request.getServerName();
                final Object obj = this.serverMap.get(serverName);
                final Method method = obj.getClass().getMethod(request.getMethodName(), request.getArgsClassTypes());
                this.executorService.execute(() -> {
                    Object response = null;
                    try {
                        response = method.invoke(obj, request.getArgs());
                    } catch (final Exception ignored) {
                    }
                    final ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setSeq(request.getSeq());
                    responseMessage.setResultCode(ResultCode.SUCCESS);
                    responseMessage.setResponseObject(response);
                    channel.write(responseMessage);
                });
            }
        } catch (final Exception e) {
            if (e instanceof RpcException) {
                if (channel.isOpen()) {
                    try {
                        channel.close();
                    } catch (final IOException ignored) {
                    }
                }
            }
        }
    }

	@Override
	public <T, I extends T> void registerService(Class<T> serviceInterface, I impl) throws Exception {
		// TODO Auto-generated method stub
		this.register(serviceInterface, impl);
	}

	












}
