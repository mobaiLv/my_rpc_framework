package github.mobai;

import github.mobai.annotation.RpcService;

/**
 * @author mobai
 * @date 2022/3/6
 */
@RpcService(group = "group1", version = "v1")
public class HelloServiceImpl implements HelloService{

    static {
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String hello(Hello greet) {
        System.out.println("HelloServiceImpl收到：" + greet.getMessage());
        String result = "Hello Description is: " + greet.getDetail();
        System.out.println("HelloServiceImpl返回：" + result);
        return result;
    }
}
