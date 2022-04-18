package github.mobai.loadbalance;

import github.mobai.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡策略抽象类
 *
 * @author mobai
 * @date 2022/3/3
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddress, RpcRequest rpcRequest) {
        if (serviceAddress == null || serviceAddress.size() == 0) {
            return null;
        }
        if (serviceAddress.size() == 1) {
            return serviceAddress.get(0);
        }
        return doSelect(serviceAddress, rpcRequest);
    }

    protected abstract String doSelect(List<String> serviceAddress, RpcRequest rpcRequest);
}
