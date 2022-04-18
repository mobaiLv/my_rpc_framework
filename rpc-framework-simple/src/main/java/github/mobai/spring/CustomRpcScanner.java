package github.mobai.spring;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 自定义包扫描器
 *
 * @author mobai
 * @date 2022/3/6
 */
public class CustomRpcScanner extends ClassPathBeanDefinitionScanner {

    public CustomRpcScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

    //    @Override
//    public int scan(String... basePackages) {
//        return super.scan(basePackages);
//    }
}
