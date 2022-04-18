package github.mobai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 响应码枚举类
 *
 * @author mobai
 * @date 2022/3/2
 */
@Getter
@ToString
@AllArgsConstructor
public enum RpcResponseCodeEnum {

    SUCCESS(200, "The remote call is successful"),
    FAIL(500, "The remote call is fail");

    private final int code;
    private final String message;
}
