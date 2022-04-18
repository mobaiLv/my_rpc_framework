package github.mobai.remoting.constants;

/**
 * 常量类
 *
 * @author mobai
 * @date 2022/3/2
 */
public class RpcConstants {

    /**
     * 魔数，用于验证协议报文
     */
    public static final byte[] MAGIC_NUMBER = {(byte)'g', (byte)'r', (byte)'p', (byte)'c'};
    /**
     * 报文版本
     */
    public static final byte VERSION = 1;
    /**
     * 报文头总长
     */
    public static final byte TOTAL_LENGTH = 16;
    /**
     * 包类型 1:请求类型 2:响应类型
     */
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    /**
     * 心跳类型 3:请求心跳 4:响应心跳
     */
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    public static final int HEAD_LENGTH = 16;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    /**
     * 长度解码器最大帧长度
     */
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;

}
