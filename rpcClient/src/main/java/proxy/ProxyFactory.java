package proxy;

import netty.NettyClientInit;

import java.lang.reflect.Proxy;


public class ProxyFactory<T> {
    public T getProxy(Class<T> clazz, NettyClientInit nettyClient){
        Object proxyResult = Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new ProxyInocationHandler(clazz,  nettyClient.getChannel())
        );
        return clazz.cast(proxyResult);
    }
}
