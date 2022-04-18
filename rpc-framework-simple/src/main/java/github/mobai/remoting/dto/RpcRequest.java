package github.mobai.remoting.dto;

import com.sun.scenario.effect.impl.prism.PrImage;
import lombok.*;

import java.io.Serializable;

/**
 * 请求报文类
 *
 * @author mobai
 * @date 2022/2/24
 */
@Builder
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -1887695801347088854L;
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 接口名
     */
    private String interfaceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型列表
     */
    private Class<?>[] paramTypes;
    /**
     * 参数列表
     */
    private Object[] parameters;
    /**
     * 版本，升级兼容
     */
    private String version;
    /**
     * 分组，兼容多实现类
     */
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
