package github.mobai.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import github.mobai.exception.SerializeException;
import github.mobai.remoting.dto.RpcRequest;
import github.mobai.remoting.dto.RpcResponse;
import github.mobai.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 自定义kryo序列化实现类
 *
 * @author mobai
 * @date 2022/2/24
 */
public class KryoSerializer implements Serializer {

    /**
     * 由于 Kryo 线程不安全，每个线程都应该有自己的Kryo，Input和Output实例
     * 所以使用 ThreadLocal 存放 Kryo 对象
     */
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        // 默认值为false，是否关闭循环引用，可以提高性能，但是一般不推荐设置为true
        kryo.setReferences(true);
        // 默认值为true，是否关闭注册行为，关闭之后可能存在序列化问题，一般推荐设置为true
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("序列化失败");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
            Kryo kryo = kryoThreadLocal.get();
            Object obj = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(obj);
        } catch (Exception e) {
            throw new SerializeException("反序列化失败");
        }
    }
}
