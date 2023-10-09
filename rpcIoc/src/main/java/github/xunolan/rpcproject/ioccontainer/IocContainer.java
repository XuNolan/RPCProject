package github.xunolan.rpcproject.ioccontainer;

import github.xunolan.rpcproject.annotation.PackageScan;
import github.xunolan.rpcproject.annotation.client.RpcClient;
import github.xunolan.rpcproject.annotation.server.RpcServer;
import github.xunolan.rpcproject.annotation.server.RpcService;
import github.xunolan.rpcproject.api.ServiceApi;
import github.xunolan.rpcproject.definition.BeanDefinition;
import github.xunolan.rpcproject.extension.ExtensionLoader;
import github.xunolan.rpcproject.factory.BeanFactory;
import github.xunolan.rpcproject.loadbalance.LoadBalancer;
import github.xunolan.rpcproject.netty.NettyClientInit;
import github.xunolan.rpcproject.netty.NettyServerInit;
import github.xunolan.rpcproject.registry.BeanRegistry;
import github.xunolan.rpcproject.registry.ServiceRegistry;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

public abstract class IocContainer {
    private Class<?> clazz;
    private String[] scanPackages;
    protected BeanRegistry beanRegistry;
    protected BeanFactory beanFactory;

    public IocContainer(Class<?> clazz){
        this.clazz = clazz;
    }
    public IocContainer initIocContainer(){
        //扫描和处理注解，并进行注解实例化。
        if(this.clazz.isAnnotationPresent(PackageScan.class)){
            this.scanPackages = this.clazz.getAnnotation(PackageScan.class).Packages();
        } else {
            this.scanPackages = new String[]{this.clazz.getPackage().getName()};//当前类路径
        }
        //现在的首要问题是将Client的ioc容器和Server的ioc容器区分清楚。client的ioc容器更加复杂。先写client的。
        //注册Bean。
        initBeanRegistry(scanPackages);
        //初始化BeanFactory
        initBeanFactory(beanRegistry.getBeanDefinitions());
        return this;
    }

    public IocContainer run(){
        //RpcClient与RpcServer注解可要可不要。实际通过iocContainer实例化的类型来区分；
        if(this instanceof ClientIocContainer){
            String serviceDiscovery = "nacos";
            String loadBalance = "random";
            if(this.clazz.isAnnotationPresent(RpcClient.class)) {
                RpcClient rpcClient = this.clazz.getAnnotation(RpcClient.class);
                serviceDiscovery = rpcClient.ServiceDiscovery();
                loadBalance = rpcClient.LoadBalance();
            }
            ServiceRegistry registry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension(serviceDiscovery);
            LoadBalancer loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).getExtension(loadBalance);
            List<InetSocketAddress> addresses = registry.lookUpService(ServiceApi.class.getName());
            InetSocketAddress targetAddress = loadBalancer.getService(addresses);//这个负载均衡感觉也就是意思意思（）
            //建立远端连接
            NettyClientInit nettyClient = new NettyClientInit(targetAddress);
            nettyClient.run();

        }else if(this instanceof ServerIocContainer){
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
            if(this.clazz.isAnnotationPresent(RpcService.class)){
                RpcServer anno = this.clazz.getAnnotation(RpcServer.class);
                inetSocketAddress = new InetSocketAddress(anno.Host(), anno.Port());
            }
            ServiceRegistry serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("nacos");
            InetSocketAddress finalInetSocketAddress = inetSocketAddress;
            this.beanFactory.singletonObject.keySet().forEach(
                    x -> serviceRegistry.register(x, finalInetSocketAddress)
            );
            NettyServerInit nettyServer = new NettyServerInit(inetSocketAddress);
            nettyServer.run();
        }
        return this;
    }

    abstract void initBeanRegistry(String[] scanPackages);
    abstract void initBeanFactory(Set<BeanDefinition> beanDefinitions);
    public BeanFactory getBeanFactory(){
        return this.beanFactory;
    }

}
