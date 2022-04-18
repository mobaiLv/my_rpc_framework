package github.mobai.provider;

import github.mobai.config.RpcServiceConfig;

/**
 * 存储和提供服务对象
 *
 * @author mobai
 * @date 2022/3/4
 */
public interface ServiceProvider {

    /**
     * 缓存服务
     *
     * @param rpcServiceConfig 服务的相关信息
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * 获取目标服务
     *
     * @param rpcServiceName rpc服务名
     * @return 目标服务对象
     */
    Object getService(String rpcServiceName);

    /**
     * 服务发布注册
     *
     * @param rpcServiceConfig 服务相关信息
     */
    void publishService(RpcServiceConfig rpcServiceConfig);

}
