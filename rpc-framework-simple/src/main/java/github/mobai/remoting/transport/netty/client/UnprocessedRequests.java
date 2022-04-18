package github.mobai.remoting.transport.netty.client;

import github.mobai.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未处理的服务端请求
 *
 * @author mobai
 * @date 2022/3/4
 */
public class UnprocessedRequests {

    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_REQUEST_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_REQUEST_FUTURES.put(requestId, future);
    }

    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_REQUEST_FUTURES.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
