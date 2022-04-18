package github.mobai.serialize;

import github.mobai.extension.SPI;

/**
 * 自定义序列化接口
 *
 * @author mobai
 * @date 2022/2/24
 */
@SPI
public interface Serializer {

    /**
     * 序列化
     * @param obj 要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes 序列化后的字节数组
     * @param clazz 反序列化生成的对象类
     * @param <T>
     * @return 反序列化对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
