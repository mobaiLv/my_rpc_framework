package github.mobai.annotation;

import java.lang.annotation.*;

/**
 * rpc服务引用注解，标记在类成员属性上
 *
 * @author mobai
 * @date 2022/3/6
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    /**
     * 服务的分组，默认为空
     */
    String group() default "";

    /**
     * 服务的版本，默认为空
     */
    String version() default "";
}
