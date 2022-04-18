package github.mobai;

import github.mobai.annotation.RpcReference;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author mobai
 * @date 2022/3/6
 */
@Component
public class HelloController {

    @RpcReference(group = "group1", version = "v1")
    private HelloService helloService;

    public void hello() throws InterruptedException {
        Hello hello = Hello.builder().message("hello").detail("打招呼").build();
        String result = helloService.hello(hello);
        System.out.println("这是返回的结果: " + result);
        TimeUnit.SECONDS.sleep(10);
        for (int i = 0; i < 10; i++) {
            System.out.println("这是返回的结果: " + helloService.hello(hello));
        }
    }
}
