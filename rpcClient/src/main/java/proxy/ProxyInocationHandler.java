package proxy;

import dto.RpcRequest;
import dto.RpcResponse;
import enums.ExceptionEnum;
import enums.ResCodeEnum;
import exception.RpcException;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.SnowflakeUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class ProxyInocationHandler implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(ProxyInocationHandler.class);
    private final Class<?> clazz;
    private final Channel channel;

    public ProxyInocationHandler(Class<?> clazz, Channel channel){
        this.clazz = clazz;
        this.channel = channel;
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
            channel.closeFuture().sync();
            CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
            ResultMap.getResultMap().put(rpcRequest.getId(), resultFuture);
            log.info("线程阻塞在获取返回值中");
            result = resultFuture.get(); //阻塞，获取返回值
        }catch(InterruptedException | ExecutionException e) {
            throw new RpcException(ExceptionEnum.RpcProcessWaitFail,e);
        }

        if(result.getResCode().getCode() == ResCodeEnum.Success.getCode()) {
            return method.getReturnType().cast(result);
        }
        return null;
    }
}