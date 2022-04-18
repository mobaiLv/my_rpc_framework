package github.mobai.annotation;

import java.lang.annotation.*;

/**
 * rpc服务暴露注解，标注在服务实现类上
 *
 * @author mobai
 * @date 2022/3/6
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    /**
     * 服务的分组，默认为空
     * @return
     */
    String group() default "";

    /**
     * 服务的版本，默认为空
     */
    String version() default "";
}
