package github.xunolan.rpcproject.proxy;

import github.xunolan.rpcproject.netty.NettyClientInit;

import java.lang.reflect.Proxy;


public class ProxyFactory<T> {
    public T getProxy(Class<T> clazz){
        Object proxyResult = Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new ProxyInocationHandler(clazz)
        );
        return clazz.cast(proxyResult);
    }
}
