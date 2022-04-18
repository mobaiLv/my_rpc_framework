package github.mobai.spring;

import github.mobai.annotation.RpcReference;
import github.mobai.annotation.RpcService;
import github.mobai.config.RpcServiceConfig;
import github.mobai.extension.ExtensionLoader;
import github.mobai.factory.SingletonFactory;
import github.mobai.provider.ServiceProvider;
import github.mobai.provider.impl.ZkServiceProviderImpl;
import github.mobai.proxy.RpcClientProxy;
import github.mobai.remoting.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 使用代理类屏蔽服务引用、暴露的细节
 *
 * @author mobai
 * @date 2022/3/6
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    /**
     * 包含服务缓存和服务注册
     */
    private ServiceProvider serviceProvider;
    private RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // 构建rpc服务暴露信息
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            // 进行服务注册以及本地缓存
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        // 遍历所有字段，寻找标记了RpcReference注解的字段，生成代理类进行替换
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(RpcReference.class)) {
                RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
