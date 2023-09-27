package service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import dto.Request;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ResultMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class ServiceInocationHandler implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(ServiceInocationHandler.class);
    private final Class<?> clazz;

    private static final Snowflake snowflake = new Snowflake();
    private final Channel channel;

    public ServiceInocationHandler(Class<?> clazz, Channel channel){
        this.clazz = clazz;
        this.channel = channel;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        //构造请求；
        Request request = new Request(snowflake.nextIdStr(),
                clazz.getSimpleName(),
                method.getName(),
                method.getParameterTypes(),
                args);
        if(channel.isActive()) {
            channel.writeAndFlush(request).addListener(future -> {
                if (future.isSuccess()) {
                    log.info("client send message");
                } else {
                    log.error("Send failed:", future.cause());
                }
            });
        }
        Object result = null;
        try {
            //channel.closeFuture().sync();
            CompletableFuture<Object> resultFuture = new CompletableFuture<>();
            ResultMap.getResultMap().put(request.getId(), resultFuture);
            //阻塞，获取返回值
            result = resultFuture.get();
        }catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
        if(!ObjectUtil.isEmpty(result))
            return method.getReturnType().cast(result);
        return null;
    }
}
