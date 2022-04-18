package github.mobai.loadbalance.loadbalancer;

import github.mobai.loadbalance.AbstractLoadBalance;
import github.mobai.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡策略实现
 *
 * @author mobai
 * @date 2022/3/3
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> serviceAddress, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
