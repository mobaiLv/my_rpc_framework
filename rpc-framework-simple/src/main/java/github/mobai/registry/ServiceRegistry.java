package github.mobai.registry;

import github.mobai.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 服务注册接口
 *
 * @author mobai
 * @date 2022/3/2
 */
@SPI
public interface ServiceRegistry {

    /**
     * 注册服务
     *
     * @param rpcServiceName rpc服务名(interfaceName + group + version)
     * @param inetSocketAddress 服务地址
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
