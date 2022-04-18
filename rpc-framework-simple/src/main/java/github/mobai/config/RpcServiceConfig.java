package github.mobai.config;

import lombok.*;

/**
 * @author mobai
 * @date 2022/3/4
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceConfig {

    /**
     * 服务版本
     */
    private String version = "";
    /**
     * 当接口有多个实现类时，可以进行分组
     */
    private String group = "";
    /**
     * 目标服务
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
