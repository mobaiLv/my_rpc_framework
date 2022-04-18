package github.mobai.remoting.transport;

import github.mobai.extension.SPI;
import github.mobai.remoting.dto.RpcRequest;

/**
 * 请求传输接口
 *
 * @author mobai
 * @date 2022/3/2
 */
@SPI
public interface RpcRequestTransport {

    /**
     * 发送rpc请求到服务端，然后获取结果
     *
     * @param rpcRequest 请求消息体
     * @return 服务端返回数据
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
