package github.mobai.handler;

import github.mobai.exception.RpcException;
import github.mobai.factory.SingletonFactory;
import github.mobai.provider.ServiceProvider;
import github.mobai.provider.impl.ZkServiceProviderImpl;
import github.mobai.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * rpcRequest处理器
 *
 * @author mobai
 * @date 2022/3/6
 */
@Slf4j
public class RpcRequestHandler {

    // 服务缓存
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    /**
     * 处理rpcRequest,调用目标方法并返回调用结果
     */
    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 获取目标方法的执行结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service [{}] successful invoke method: [{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
