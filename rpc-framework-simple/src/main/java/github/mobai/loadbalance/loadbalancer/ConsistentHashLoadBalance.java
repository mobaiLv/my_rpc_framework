package github.mobai.loadbalance.loadbalancer;

import github.mobai.loadbalance.AbstractLoadBalance;
import github.mobai.remoting.dto.RpcRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一致性哈希负载均衡实现
 * refer to dubbo consistent hash load balance: https://github.com/apache/dubbo/blob/2d9583adf26a2d8bd6fb646243a9fe80a77e65d5/dubbo-cluster/src/main/java/org/apache/dubbo/rpc/cluster/loadbalance/ConsistentHashLoadBalance.java
 *
 * @author mobai
 * @date 2022/3/3
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    // 一个 RpcServiceName 对应一个哈希环
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected String doSelect(List<String> serviceAddress, RpcRequest rpcRequest) {
        // 无视List重写的hashCode()，获取默认的Object返回的hashCode
        int identityHashCode = System.identityHashCode(serviceAddress);
        // rpcServiceName: 具有唯一标识的服务名
        String rpcServiceName = rpcRequest.getRpcServiceName();
        ConsistentHashSelector selector = selectors.get(rpcServiceName);
        // 检查是否需要更新哈希环
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(rpcServiceName, new ConsistentHashSelector(serviceAddress, 160, identityHashCode));
            selector = selectors.get(rpcServiceName);
        }
        // 使用具有唯一标识的key做hash，从哈希环中顺时针取临近的第一个节点
        return selector.select(rpcServiceName + Arrays.stream(rpcRequest.getParameters()));
    }

    static class ConsistentHashSelector {
        private final TreeMap<Long, String> virtualInvokers;

        private final int identityHashCode;

        /**
         *
         * @param invokers 待负载均衡的IP列表
         * @param replicaNumber 虚拟节点数量
         * @param identityHashCode 忽略重写的hashCode方法，使用默认的Object返回的hashCode，这里是rpcServiceName的hashCode
         */
        ConsistentHashSelector(List<String> invokers, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            // invoker相当于真实节点，映射了哈希环上的160个虚拟节点
            for (String invoker : invokers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    // 以 IP + 递增数字做MD5,以此作为标识
                    byte[] digest = md5(invoker + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        public String select(String rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey);
            return selectForKey(hash(digest, 0));
        }

        public String selectForKey(long hashCode) {
            // tailMap(K fromKey) 获取一个子集。其所有对象的 key 的值大于等于 fromKey
            // ceilingEntry(K fromKey) 直接获取大于等于 fromKey 的 Entry
            Map.Entry<Long, String> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }
    }
}
