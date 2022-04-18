package github.mobai;

import github.mobai.annotation.RpcScan;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author mobai
 * @date 2022/3/6
 */
@RpcScan(basePackage = {"github.mobai"})
public class NettyClientMain {

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = applicationContext.getBean(HelloController.class);
        helloController.hello();
    }
}
