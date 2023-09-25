package service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResultMap {
    private static Map<String, CompletableFuture<Object>> resultMap = new HashMap<>();
    public static Map<String, CompletableFuture<Object>> getResultMap(){
        return resultMap;
    }
}
