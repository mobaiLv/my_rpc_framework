package github.mobai.annotation;

import github.mobai.spring.CustomRpcScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 扫描自定义注解
 *
 * @author mobai
 * @date 2022/3/6
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Import(CustomRpcScannerRegistrar.class)
public @interface RpcScan {

    String[] basePackage();
}
