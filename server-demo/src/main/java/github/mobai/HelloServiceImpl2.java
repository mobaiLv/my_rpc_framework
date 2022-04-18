package github.mobai;

import github.mobai.annotation.RpcService;

/**
 * @author mobai
 * @date 2022/3/6
 */
public class HelloServiceImpl2 implements HelloService{

    static {
        System.out.println("HelloServiceImpl2被创建");
    }

    @Override
    public String hello(Hello greet) {
        System.out.println("HelloServiceImpl2收到：" + greet.getMessage());
        String result = "Hello Description is: " + greet.getDetail();
        System.out.println("HelloServiceImpl2返回：" + result);
        return result;
    }
}
