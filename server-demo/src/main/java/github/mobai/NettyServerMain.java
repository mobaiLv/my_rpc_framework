package github.mobai;

import github.mobai.annotation.RpcScan;
import github.mobai.config.RpcServiceConfig;
import github.mobai.remoting.transport.netty.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author mobai
 * @date 2022/3/6
 */
@RpcScan(basePackage = {"github.mobai"})
public class NettyServerMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = applicationContext.getBean(NettyRpcServer.class);
        // 手动注册
        HelloService helloService2 = new HelloServiceImpl2();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                .group("group2")
                .version("v2")
                .service(helloService2).build();
        nettyRpcServer.registerService(rpcServiceConfig);
        nettyRpcServer.start();
    }
}
