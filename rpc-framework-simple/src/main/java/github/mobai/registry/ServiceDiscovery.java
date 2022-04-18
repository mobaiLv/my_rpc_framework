package github.mobai.registry;

import github.mobai.extension.SPI;
import github.mobai.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务发现
 *
 * @author mobai
 * @date 2022/3/2
 */
@SPI
public interface ServiceDiscovery {

    /**
     * 通过 rpcServiceName 发现服务
     *
     * @param rpcRequest rpc请求对象
     * @return 服务地址
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
