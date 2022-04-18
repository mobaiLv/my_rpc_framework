package github.mobai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author mobai
 * @date 2022/3/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hello implements Serializable {

    private String message;
    private String detail;
}
