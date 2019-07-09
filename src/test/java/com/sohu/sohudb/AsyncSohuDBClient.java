package com.sohu.sohudb;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

import lava.rt.aio.tcp.TcpQueryClient;
import lava.rt.logging.Log;
import lava.rt.logging.LogFactory;



public class AsyncSohuDBClient extends TcpQueryClient {
	
	private static byte[] zeroData = {};
	
	

	protected ByteBuffer[] outBuffs = null;
	protected ByteBuffer[] inBuffs = null;

	/**
	 * ���캯��
	 */
	AsyncSohuDBClient(){
		initBuffer();
	}

	/**
	 * ����ͨѶЭ���ʼ���շ�������
	 * GET:
	 * ���ͻ�����: | 1 byte | 4 bytes | dynamic bytes | 4 bytes | 
	 *             CMD��     key_LEN       KEY         FLAGS
	 *             ��Ϊ01   URL byte����    URL         ��Ϊ0
	 * PUT:
	 * ���ͻ�����: | 1 byte | 4 bytes | dynamic bytes | 4 bytes | dynamic bytes | 2 bytes | 4 bytes  
	 *             CMD��     key_LEN       KEY          DATA_LEN   DATA          extension  FLAGS
	 *             ��Ϊ01   URL byte����    URL         ��Ϊ0       DATA           Ϊ0        0
	 * ���ջ�����: | 4 bytes | 4 bytes | dynamic bytes |
	 *             status    VAL_LEN       KEY
	 *            -1, 0, 1   ���ݳ���     ��������
	 *            0��ʾ����
	 * ���������Ǿ���Zip�㷨ѹ��������.��Ҫ�Ƚ�ѹ.��ѹ�ɵ��ַ�����ʽ����:
	 * {url}\n
	 * {xxx}\n
	 * ҳ������
	 *
	 */
	private void initBuffer(){
		ByteBuffer[] outBuffers = new ByteBuffer[5];
		outBuffers[0] = ByteBuffer.allocate(5);
		outBuffers[1] = null; // key;
		outBuffers[2] = ByteBuffer.allocate(4);
		outBuffers[3] = null; // send value;
		outBuffers[4] = ByteBuffer.allocate(6);
		
		for( int i=0; i< outBuffers.length; i++){
			if( outBuffers[i] == null ) continue;
			outBuffers[i].order( ByteOrder.BIG_ENDIAN );
		}

		ByteBuffer[] inBuffers = new ByteBuffer[3];
		inBuffers[0] = ByteBuffer.allocate(4);
		inBuffers[1] = ByteBuffer.allocate(4);
		inBuffers[2] = null; // receive buffer
		for( int i=0; i< inBuffers.length; i++){
			if( inBuffers[i] == null ) continue;
			inBuffers[i].order( ByteOrder.BIG_ENDIAN );
		}
		
		this.outBuffs = outBuffers;
		this.inBuffs = inBuffers;
	}
	
	private static final int getDataLen(int keyLen, int arrayLen){
		return keyLen >= 0 ? Math.min(keyLen, arrayLen) : arrayLen;
	}
	
	public int sendRequest() throws IOException {
		Log log = getLogger();
		this.life --;
		
		SocketChannel channel = getChannel();
		if( channel == null ) return -1;
		
		SohuDBRequest qr = (SohuDBRequest) this.getRequest();
		
		byte [] dataBuf=null;
		
		if("summary".equals(qr.getReqType()))
			dataBuf = new byte[2*1024*1024];
		else
			dataBuf = new byte[512*1024];
		
		qr.setValue(dataBuf);
		
		byte[]key = null;
		int keyLen = 0;
		byte[] value = null;
		int valueLen = 0;
		RequestType cmd = null;

		do {
			if( qr == null ) break;
			cmd = qr.getCmd();
			if( cmd == null ) break;
			
			if( cmd != RequestType.CMD_GET 
					&& cmd != RequestType.CMD_PUT
					&& cmd != RequestType.CMD_DEL)
				break;

			key = qr.getKey();
			keyLen = qr.getKeyLen();
			value = qr.getValue();
			valueLen = qr.getValueLen();
			if( value == null ) value = zeroData;
			//if( key == null ) break;
		}while(false);
		if( key == null ) return 0;
		if( value == null ) value = zeroData;
		
		try{
			outBuffs[0].put((byte)cmd.ordinal());

			int len = getDataLen(keyLen, key.length);
			outBuffs[0].putInt( len );
			outBuffs[0].flip();
			
			outBuffs[1] = ByteBuffer.wrap(key, 0, len );
			
			if( cmd == RequestType.CMD_PUT ){
				len = getDataLen(valueLen, value.length);
				outBuffs[2].putInt( len );
				outBuffs[2].flip();
				
				outBuffs[3] = ByteBuffer.wrap(value, 0, len);
	
				outBuffs[4].putShort((short)0xfda1); // digest���ݲ�֧��.
			} else {
				outBuffs[2].flip();
				outBuffs[3] = ByteBuffer.wrap(zeroData, 0, zeroData.length);
			}
			
			outBuffs[4].putInt(qr.getFlag()); // flag��
			outBuffs[4].flip();
	
			// ��������
			int total_len = 0;
			for(int i=0;i< outBuffs.length; i++){
				total_len += outBuffs[i].remaining();
			}
			int remaining = total_len;
			// write����0ʧ�ܵĴ���
			int retry = 0;
			try{
				while( remaining > 0 ){
					long n = channel.write( outBuffs );
					remaining -= n;
					if( n == 0 ) retry ++;
					if( retry > 6 ) {
						// jvm������bug������⵽ĳ��channelʼ�շ��Ͳ���ȥ��
						String msg = "Can't Send Data thru SChannel! Bug maybe... retry " + retry + " times.";
						if( log != null  ){
							log.error("luke:" + msg);
						}
						throw new IOException(msg);
					}
				}
			}catch( IllegalArgumentException e){
				// ����jdk��bug, ��bug��1.6.0_u1���޸�.
				throw (IOException)new IOException().initCause(e);
			}
	
			return total_len;
		}catch( BufferOverflowException  e){
			if( log != null ){
				log.info("TinyBufferOverflow:",e);
			}
		}
		return 0;
	}

	/**
	 * �ӷ����������ղ�ѯ���.
	 * @return
	 */
	public int handleInput() throws IOException
	{
		SocketChannel channel = getChannel();
		if( channel == null ) throw new IOException("getChannel() failed!");

		if( inBuffs[2] == null ){
			SohuDBRequest qr = (SohuDBRequest) this.getRequest();
			if( qr == null ) throw new IOException("getRequest() failed!");
			byte[] value = qr.getValue();
			int valueLen = qr.getValueLen();
			if( value == null ) value = zeroData;
			int len = getDataLen( valueLen, value.length );
			inBuffs[2] = ByteBuffer.wrap(value, 0, len);
			inBuffs[2].clear();
		}

		long n=0;
		try{
			n = channel.read(inBuffs);
		}catch( BufferOverflowException e){
			throw (IOException)new IOException().initCause(e);
		}
		return (int)n;
	}

	private static final int TYPE_NO_DATA = 1;
	private static final int TYPE_HAS_DATA = 0;
	private static final int TYPE_ERROR = -1;
	
	public boolean finishResponse() throws IOException{

		SohuDBRequest req = (SohuDBRequest)getRequest();

		if( req == null || req.getCmd() == null ){
			throw new IOException("request.getCmd() is not valid"); // should never happen
		}

		int dataType = TYPE_NO_DATA;
		int code = 0;
		if (inBuffs[0].remaining() > 0)
			return false;
		
//		inBuffs[0].flip();
		code = inBuffs[0].getInt(0);
//		inBuffs[0].rewind();
		if (req.getCmd() == RequestType.CMD_GET) {
			switch (code) {
			case 0: // ������
				dataType = TYPE_HAS_DATA;
				break;
			case 1: // û������
				dataType = TYPE_NO_DATA;
				break;
			case -1: // ����
			default:
				dataType = TYPE_ERROR;
			}
		} else if (req.getCmd() == RequestType.CMD_PUT) {
			switch (code) {
			case 0: // ����ɹ�������ԭ��û�д�������.
			case 1: // ����ɹ�������ԭ����������.
				dataType = TYPE_NO_DATA;
				break;
			case -1: // ���ʧ��
			default:
				dataType = TYPE_ERROR;
			}
		} else if (req.getCmd() == RequestType.CMD_DEL) {
			switch (code) {
			case 0:
			case 1:
				dataType = TYPE_NO_DATA;
				break;
			case -1:
			default:
				dataType = TYPE_ERROR;
			}
		} else { // ���������ݲ�֧��
			throw new IOException("Not Supported SohuDB operation:"
					+ req.getCmd().toString());
		}

		boolean ret = false;
		int len = -1;
		switch (dataType) {
		case TYPE_NO_DATA:
			if (inBuffs[1].position() != 0)
				throw new IOException(
						"Code is 1 and DataLen > 0, Possible Server BUG!");
			ret = true;
			break;
		case TYPE_HAS_DATA :
			if( inBuffs[1].remaining() > 0 ){
				// �����δ���꣬�ع�����λ�ã��ȴ��´δ���
				// ret = false;
			} else {
//				inBuffs[1].flip();
				len = inBuffs[1].getInt(0);
				//inBuffs[1].rewind();

				if( inBuffs[2] == null ){ // �ݴ������inBuffs[2]û��������ֵ������Ϊ���������Ѿ����.
					throw new IOException("inBuffs[2] not properly filled");
				} else {
					int curLen = inBuffs[2].position();
					if( curLen == len ){
						ret = true;
					} else if( curLen < len ){
						if( curLen == inBuffs[2].limit() ){ // ��黺�����Ƿ�������������
							throw new IOException("byte[]value has not Enough Space!");
						}
						// ret = false;
					} else { //  curLen > len
						throw new IOException("Received Data is bigger than DATA_LEN field��Possiblly Server BUG!");
					}
				}
			}
			break;
		case TYPE_ERROR:
			if( inBuffs[1].remaining() > 0 ){
				// �����δ���꣬�ع�����λ�ã��ȴ��´δ���
				// ret = false;
			} else {
//				inBuffs[1].flip();
				len = inBuffs[1].getInt(0);
//				inBuffs[1].rewind();
				ret = true;
			}
			break;
		default:
			throw new IOException("DATA Type is not VALID! Check Your Code!"+ dataType);
		}
		
		if( ret ){ // ����Ѿ��ɹ���ȡ 
			SohuDBResult rt = new SohuDBResult();
			rt.setData(inBuffs[2].array());
			rt.setStatus(code);
			rt.setLen(len);
			req.setResult(rt);
		}
		return ret;
	}


	public void reset() throws IOException {
		outBuffs[0].clear();
		outBuffs[1] = null; // key;
		outBuffs[2].clear();
		outBuffs[3] = null; // send value;
		outBuffs[4].clear();
		
		inBuffs[0].clear();
		inBuffs[1].clear();
		inBuffs[2] = null; // recv data
		setRequest( null );
	}

	@Override
	protected Log getLogger() {
		
		return LogFactory.SYSTEM.getLog(AsyncSohuDBClient.class);
	}
}
