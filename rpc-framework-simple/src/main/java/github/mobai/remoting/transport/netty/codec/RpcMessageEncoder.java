package github.mobai.remoting.transport.netty.codec;

import github.mobai.compress.Compress;
import github.mobai.enums.CompressTypeEnum;
import github.mobai.enums.SerializationTypeEnum;
import github.mobai.extension.ExtensionLoader;
import github.mobai.remoting.constants.RpcConstants;
import github.mobai.remoting.dto.RpcMessage;
import github.mobai.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 自定义协议的编码器
 * <p>
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code      |version |          full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 *
 * @author mobai
 * @createTime on 2022/3/2
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage rpcMessage, ByteBuf out) {
        try {
            // 按顺序写入协议中的数据
            // 1.魔数
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            // 2.版本
            out.writeByte(RpcConstants.VERSION);
            // 3.长度字段先跳过,写位置向后移4个字节，待计算完数据长度再写入
            out.writerIndex(out.writerIndex() + 4);
            // 4.消息类型
            byte messageType = rpcMessage.getMessageType();
            out.writeByte(messageType);
            // 5.序列化类型
            out.writeByte(rpcMessage.getCodec());
            // 6.压缩类型
            out.writeByte(rpcMessage.getCompress());
            // 7.requestId
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());
            // 计算报文总长
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // 非心跳类型才计算数据体长度 fullLength = headLength + bodyLength
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                // 序列化
                bodyBytes = serializeBody(rpcMessage.getCodec(), rpcMessage.getData());
                // 压缩
                bodyBytes = compressBody(rpcMessage.getCompress(), bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                // 8.body
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            // full length 设置写位置到魔数和版本之后，写入长度
            out.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("Encode request error", e);
        }
    }

    private byte[] serializeBody(byte codecType, Object body) {
        String codecName = SerializationTypeEnum.getName(codecType);
        log.info("codec name: [{}]", codecName);
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                .getExtension(codecName);
        return serializer.serialize(body);
    }

    private byte[] compressBody(byte compressType, byte[] serializeBody) {
        String compressName = CompressTypeEnum.getName(compressType);
        Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                .getExtension(compressName);
        return compress.compress(serializeBody);
    }
}
