package github.mobai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * rpc配置枚举类
 *
 * @author mobai
 * @date 2022/3/3
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    RPC_CONFIG_PATH("rpc.properties"),
    ZK_ADDRESS("rpc.zookeeper.address");

    private final String propertyValue;
}
