package lava.rt.async;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;

public abstract class AsyncQueryClient <CH extends Channel> {

	/**
	 * 由 Receiver 回调，当 Receiver 发现对应的 SocketChannel 有数据可读的时候，
就会调用该函数完成数据收取操作。这样做是因为，异步连接池并不关心接收
缓冲区如何组织，你可以用多个 Buffer 来组织接收数据，实现的时候根据自己
的意愿把数据填充到不同的 Buffer 中，也可以乘此机会做一些其他的工作，只
要保证该函数尽可能快的完成就是了。
	 * @throws IOException
	 */
	public abstract void handleInput(SelectionKey key) throws  IOException;
	
	
	/*
	 * 回调函数，可能由 Sender， Receiver 或者用户处理线程调用。该函数完成数据的发送逻辑。
	 */
	public abstract void sendRequest(CH channel) throws  IOException;
	
	/*
	 * 回调函数，由 Receiver 调用。询问该 Client，数据接收工作是否已经完成，即
是否已经接收了一组完整的响应数据。该函数完成两个工作：一是确认数据已
经接收完成，二是将对数据进行处理，因为缓冲区要腾出来供下次请求使用，
数据的解析工作要放在这里执行。
	 */
	public void finishResponse() {
		
	}
	
	/*
	 * 回调函数，由 Receiver 调用。 Client 类是个状态相关的类，具体实例化的时候，
你可能会设置一些状态数据，这里允许你对状态数据清零，以备下次使用。
	 */
	public void reset() {
		
	}
	
}
