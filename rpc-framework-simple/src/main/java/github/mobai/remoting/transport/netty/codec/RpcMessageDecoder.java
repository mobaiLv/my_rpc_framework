package github.mobai.remoting.transport.netty.codec;

import github.mobai.compress.Compress;
import github.mobai.enums.CompressTypeEnum;
import github.mobai.enums.SerializationTypeEnum;
import github.mobai.extension.ExtensionLoader;
import github.mobai.remoting.constants.RpcConstants;
import github.mobai.remoting.dto.RpcMessage;
import github.mobai.remoting.dto.RpcRequest;
import github.mobai.remoting.dto.RpcResponse;
import github.mobai.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 自定义协议解码器
 *
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+--------+-----+-----+-------+
 *   |   magic   code        |version |           full length       |messageType| codec|compress|  RequestId |
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
 * <p>
 * {@link LengthFieldBasedFrameDecoder} 是一个基于长度的解码器 , 用于解决 TCP 的 拆包/粘包 问题
 * </p>
 *
 * @author mobai
 * @createTime on 2022/3/2
 * @see <a href="https://zhuanlan.zhihu.com/p/95621344">LengthFieldBasedFrameDecoder解码器</a>
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        // lengthFieldOffset: 魔数4字节，版本1字节，然后就是报文长度，所以拿到长度需要偏移5字节
        // lengthFieldLength: 报文长度需要4个字节存储，所以是4
        // lengthAdjustment: 读取完整的数据之前要读取9个字节，所以 数据长度 = (fullLength-9)，所以是-9
        // initialBytesToStrip: 魔数和版本信息需要手动检查，所以不能跳过这部分的byte数据，所以是0
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            // 必须大于最小报文头，才是有效报文，才能进行解码
            if (frame.readableBytes() >= RpcConstants.HEAD_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error!", e);
                    throw e;
                } finally {
                    // 释放内存
                    frame.release();
                }
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);
        // 报文总长
        int fullLength = in.readInt();
        // 构造RpcMessage对象
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        // 在发送方构建请求对象的时候指定
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .messageType(messageType)
                .codec(codecType)
                .requestId(requestId).build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            // 数据解压
            byte[] bs = new byte[bodyLength];
            in.readBytes(bs);
            String compressName = CompressTypeEnum.getName(compressType);
            // 根据报文中的压缩类型找到对应的实现类，进行解压
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                    .getExtension(compressName);
            bs = compress.decompress(bs);
            // 反序列化
            String codecName = SerializationTypeEnum.getName(codecType);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);
            if (messageType == RpcConstants.REQUEST_TYPE) {
                RpcRequest req = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(req);
            } else {
                RpcResponse resp = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(resp);
            }
        }
        return rpcMessage;
    }

    private void checkMagicNumber(ByteBuf in) {
        // 读取最开头的4个字节，这个4个字节是魔数，然后进行比较
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (RpcConstants.MAGIC_NUMBER[i] != tmp[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }
}
