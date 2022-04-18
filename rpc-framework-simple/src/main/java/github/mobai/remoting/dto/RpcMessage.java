package github.mobai.remoting.dto;

import lombok.*;

/**
 * rpc消息对象
 *
 * @author mobai
 * @date 2022/3/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    /**
     * rpc消息类型
     */
    private byte messageType;
    /**
     * 序列化类型
     */
    private byte codec;
    /**
     * 压缩类型
     */
    private byte compress;
    /**
     * 请求id
     */
    private int requestId;
    /**
     * 数据
     */
    private Object data;
}
