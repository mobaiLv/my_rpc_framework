package github.mobai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化枚举类
 *
 * @author mobai
 * @date 2022/3/2
 */
@AllArgsConstructor
@Getter
public enum SerializationTypeEnum {

    KYRO((byte) 0x01, "kyro"),
    PROTOSTUFF((byte) 0x02, "protostuff");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.getName();
            }
        }
        return null;
    }
}
