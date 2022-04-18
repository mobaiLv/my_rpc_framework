package github.mobai.loadbalance;

import github.mobai.extension.SPI;
import github.mobai.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡接口
 *
 * @author mobai
 * @date 2022/3/3
 */
@SPI
public interface LoadBalance {

    /**
     * 从现有服务地址列表中选择一个
     *
     * @param serviceAddress 服务地址列表
     * @return 目标服务地址
     */
    String selectServiceAddress(List<String> serviceAddress, RpcRequest rpcRequest);
}
