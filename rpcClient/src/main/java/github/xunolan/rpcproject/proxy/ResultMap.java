package github.xunolan.rpcproject.proxy;

import github.xunolan.rpcproject.dto.RpcResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResultMap {
    private static final Map<String, CompletableFuture<RpcResponse>> resultMap = new HashMap<>();
    public static Map<String, CompletableFuture<RpcResponse>> getResultMap(){
        return resultMap;
    }
}
