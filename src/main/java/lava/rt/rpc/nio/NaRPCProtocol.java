package lava.rt.rpc.nio;



import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import lava.rt.wrapper.LoggerWrapper;

public class NaRPCProtocol {
    public static final int HEADERSIZE = Integer.BYTES + Long.BYTES;
    private static final Logger LOG = LoggerWrapper.CONSOLE.self;

    public static void makeMessage(long ticket, NaRPCMessage message, ByteBuffer buffer) throws IOException {
        buffer.clear().position(HEADERSIZE);
        int size = message.write(buffer);
        buffer.flip();
        if ((size + HEADERSIZE) != buffer.remaining()) {
            throw new IOException("Error in serialization");
        }

        buffer.clear();
        buffer.putInt(size);
        buffer.putLong(ticket);
        buffer.clear().limit(HEADERSIZE + size);
    }

    public static long fetchBuffer(SocketChannel channel, ByteBuffer buffer) throws IOException{
        buffer.clear().limit(HEADERSIZE);
        int ret = channel.read(buffer);
        if (ret <= 0){
            return ret;
        }
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) < 0){
                return -1;
            }
        }
        buffer.flip();
        int size = buffer.getInt();
        long ticket = buffer.getLong();
        buffer.clear().limit(size);
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) < 0) {
                throw new IOException("error when reading header from socket");
            }

        }
        buffer.flip();
//		LOG.info("fetching message with ticket " + ticket + ", threadid " + Thread.currentThread().getName());
        return ticket;
    }
}
