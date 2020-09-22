package lava.rt.rpc.aio;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



/**
 * 序列化器
 *
 * @author peiyu
 */
public interface ISerializer {

    /**
     * 反序列化
     *
     * @param bytes        序列化数据
     * @param messageClass 序列化对象类型
     * @return 序列化对象
     * @throws IOException            异常
     * @throws ClassNotFoundException 异常
     */
    <M extends IMessage> M encoder(byte[] bytes, Class<M> messageClass) throws IOException, ClassNotFoundException;

    /**
     * 序列化ß
     *
     * @param message 序列化对象
     * @return 序列化数据
     * @throws IOException 异常
     */
    byte[] decoder(IMessage message) throws IOException;
    
    
    
    
    
    public class JdkSerializer implements ISerializer {

        @Override
        public <M extends IMessage> M encoder(final byte[] bytes, final Class<M> messageClass) throws IOException, ClassNotFoundException {
            final M message;
            
            try(final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);){
                message = (M) objectInputStream.readObject();
            }
           
            return message;
        }

        @Override
        public byte[] decoder(final IMessage message) throws IOException {
            final byte[] bytes;
            try (
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);){
            objectOutputStream.writeObject(message);
            bytes = outputStream.toByteArray();
            }
            
            return bytes;
        }
    }
    
    
    
    
}
