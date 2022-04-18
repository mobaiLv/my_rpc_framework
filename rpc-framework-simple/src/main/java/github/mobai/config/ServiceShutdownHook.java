package github.mobai.config;

import github.mobai.registry.zk.utils.CuratorUtils;
import github.mobai.remoting.transport.netty.server.NettyRpcServer;
import github.mobai.utils.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * 当服务端关闭时，删除所有已注册的服务
 *
 * @author mobai
 * @date 2022/3/4
 */
@Slf4j
public class ServiceShutdownHook {

    private static final ServiceShutdownHook CUSTOM_SHUTDOWN_HOOK = new ServiceShutdownHook();

    public static ServiceShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAllRegistry() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException e) {
            }
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }
}
