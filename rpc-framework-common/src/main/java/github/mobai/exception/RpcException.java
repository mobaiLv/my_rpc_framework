package github.mobai.exception;

import github.mobai.enums.RpcErrorMessageEnum;

/**
 * Rpc异常类
 *
 * @author mobai
 * @date 2022/3/3
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
