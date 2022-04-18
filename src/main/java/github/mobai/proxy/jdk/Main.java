package github.mobai.proxy.jdk;

/**
 * @author mobai
 * @date 2022/3/1
 */
public class Main {

    public static void main(String[] args) {
        SmsService smsService = (SmsService) JdkProxyFactory.getProxy(new SmsServiceImpl());
        smsService.send("hello");
    }
}
