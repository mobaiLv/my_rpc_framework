package github.mobai.proxy.jdk;

/**
 * @author mobai
 * @date 2022/3/1
 */
public class SmsServiceImpl implements SmsService {

    @Override
    public String send(String message) {
        System.out.println("send message:" + message);
        return message;
    }
}
