package service.impl;

import api.ServiceApi;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dto.Request;
import io.netty.channel.Channel;
import netty.ClientInit;
import service.ResultMap;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class ServiceImpl implements ServiceApi {
    private static final Logger log = LoggerFactory.getLogger(ServiceImpl.class);
    private static Snowflake snowflake = new Snowflake();
    private ClientInit nettyClient;
    public ServiceImpl(ClientInit clientInit){
        this.nettyClient = clientInit;
    }
    @Override
    public String hello(String content, int id) {
        log.info("开始调用hello方法");
        //在这里进行接口实现转request调用。
        Request request = new Request(
                snowflake.nextIdStr(),
                "ServiceApi",
                "hello",
                new Class[]{String.class,int.class},
                new Object[]{content,id}
                );
        //调用netty接口进行发送。
        Channel channel = nettyClient.getChannel();
        if(channel.isActive()) {
            channel.writeAndFlush(request).addListener(future -> {
                if (future.isSuccess()) {
                    log.info(String.format("client send message"));
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
        }catch(InterruptedException |  ExecutionException e){
            e.printStackTrace();
        }
        if(!ObjectUtil.isEmpty(result))
            return String.class.cast(result);
        return null;
    }
}
