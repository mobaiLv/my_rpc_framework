package github.mobai.registry.zk;

import github.mobai.enums.RpcErrorMessageEnum;
import github.mobai.exception.RpcException;
import github.mobai.extension.ExtensionLoader;
import github.mobai.loadbalance.LoadBalance;
import github.mobai.registry.ServiceDiscovery;
import github.mobai.registry.zk.utils.CuratorUtils;
import github.mobai.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 基于zookeeper的服务发现
 *
 * @author mobai
 * @date 2022/3/3
 */
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    private LoadBalance loadBalance;
    private static final String DEFAULT_LOAD_BALANCE = "consistentHash";

    public ZkServiceDiscoveryImpl() {
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
                .getExtension(DEFAULT_LOAD_BALANCE);
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        // 调用服务的唯一标识
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (null == serviceUrlList || serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // 负载均衡
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] addressArray = targetServiceUrl.split(":");
        String host = addressArray[0];
        int port = Integer.parseInt(addressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
