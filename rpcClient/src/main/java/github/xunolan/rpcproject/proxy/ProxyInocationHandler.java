package github.xunolan.rpcproject.proxy;

import github.xunolan.rpcproject.dto.RpcRequest;
import github.xunolan.rpcproject.dto.RpcResponse;
import github.xunolan.rpcproject.enums.ExceptionEnum;
import github.xunolan.rpcproject.enums.ResCodeEnum;
import github.xunolan.rpcproject.exception.RpcException;
import github.xunolan.rpcproject.netty.NettyClientInit;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import github.xunolan.rpcproject.utils.SnowflakeUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class ProxyInocationHandler implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(ProxyInocationHandler.class);
    private static Channel channel = NettyClientInit.getChannel();
    private final Class<?> clazz;

    public ProxyInocationHandler(Class<?> clazz){
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest rpcRequest = new RpcRequest(
                SnowflakeUtil.getSnowflakeId(), clazz.getSimpleName(),
                method.getName(), method.getParameterTypes(), args);

        if(channel.isActive()) {
            channel.writeAndFlush(rpcRequest).addListener(future -> {
                if (future.isSuccess()) {
                    log.info("client send message");
                } else {
                    log.error("Send failed:", future.cause());
                }
            });
        }
        RpcResponse result;
        try {
            CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
            ResultMap.getResultMap().put(rpcRequest.getId(), resultFuture);
            result = resultFuture.get(); //阻塞，获取返回值
        }catch(InterruptedException | ExecutionException e) {
            throw new RpcException(ExceptionEnum.RpcProcessWaitFail,e);
        }

        if(result.getResCode().getCode() == ResCodeEnum.Success.getCode()) {
            return method.getReturnType().cast(result.getResData().getResult());
        }
        return null;
    }
}
