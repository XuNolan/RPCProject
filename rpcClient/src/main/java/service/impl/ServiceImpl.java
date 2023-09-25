package service.impl;

import api.ServiceApi;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.ObjectUtil;
import dto.Request;
import lombok.extern.slf4j.Slf4j;
import netty.ClientInit;
import service.ResultMap;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
@Slf4j
public class ServiceImpl implements ServiceApi {
    private SocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
    private ClientInit nettyClient = new ClientInit(inetSocketAddress);
    private static Snowflake snowflake = new Snowflake();
    @Override
    public String hello(String content, int id) {
        log.info("开始调用hello方法");
        //在这里进行接口实现转request调用。
        Request request = new Request(
                snowflake.nextIdStr(),
                "ServiceApi",
                "hello",
                new Class[]{String.class,Integer.class},
                new Object[]{content,id}
                );
        //调用netty接口进行发送。
        nettyClient.getChannel().writeAndFlush(request);
        log.info("request已写入");
        CompletableFuture<Object> resultFuture = new CompletableFuture<>();
        ResultMap.getResultMap().put(request.getId(), resultFuture);
        Object result = null;
        try {
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
