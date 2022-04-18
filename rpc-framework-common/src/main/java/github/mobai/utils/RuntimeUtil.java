package github.mobai.utils;

/**
 * @author mobai
 * @date 2022/3/4
 */
public class RuntimeUtil {

    /**
     * 获取cpu核心数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
