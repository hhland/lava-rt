package lava.rt.async;

import java.nio.channels.Channel;

public abstract class AsyncRequest {

	
	
	
	
	public abstract  AsyncQueryClient getClient();
	
	/*
	 * 在用户处理线程掉用线程池的发送方法时被掉用，该函数用于实现分环策略。
参数值是当前可用的服务器数量
	 */
	abstract int  getServerId(int servers_count);
	
	/*
	 * 在请求发送前，允许做一次自我检测，这样可以避免非法请求跟合法请求抢资
源。当然了，你的实现中可以令返回值恒为 true，这个是没有关系的，调用
AsyncGenericQueryClient 的 sendRequest 方法还有一次检查的机会
	 */
	public boolean isValid() {
		return true;
	}
	
	
}
