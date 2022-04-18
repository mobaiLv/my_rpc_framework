package github.mobai.proxy.jdk;

import java.lang.reflect.Proxy;

/**
 * @author mobai
 * @date 2022/3/1
 */
public class JdkProxyFactory {

    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new MyInvocationHandler(target)
        );
    }
}
