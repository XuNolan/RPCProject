package github.xunolan.rpcproject;

import github.xunolan.rpcproject.annotation.client.RpcClient;
import github.xunolan.rpcproject.annotation.server.RpcServer;
import github.xunolan.rpcproject.api.ServiceApi;
import github.xunolan.rpcproject.definition.ClientBeanDefinition;
import github.xunolan.rpcproject.definition.ServiceBeanDefinition;
import github.xunolan.rpcproject.extension.ExtensionLoader;
import github.xunolan.rpcproject.loadbalance.impl.RandomLoadBalance;
import github.xunolan.rpcproject.netty.NettyClientInit;
import github.xunolan.rpcproject.netty.NettyServerInit;
import github.xunolan.rpcproject.register.LocalServiceRecord;
import github.xunolan.rpcproject.registry.ClientBeanRegistry;
import github.xunolan.rpcproject.registry.ServiceBeanRegistry;
import github.xunolan.rpcproject.registry.ServiceRegistry;
import github.xunolan.rpcproject.utils.ClassUtil;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IocContainer {
    private Set<ServiceBeanDefinition> serviceBeanDefinitions = new HashSet<>();
    private Set<ClientBeanDefinition> clientBeanDefinitions = new HashSet<>();
    //todo:ioc容器作为父类，引入serverIoc和clientIoc
    private Class<?> clazz;
    public IocContainer(Class<?> clazz){
        this.clazz = clazz;
    }
    public IocContainer initIocContainer(){ //这里负责Bean注册；
        if(this.clazz.isAnnotationPresent(RpcServer.class)) { //服务端；
            RpcServer annotation = this.clazz.getAnnotation(RpcServer.class);
            String[] packets = annotation.ServicePacketScan();
            Set<Class<?>> RpcServiceInnotedClasses = new HashSet<>();
            for (String packet : packets){
                RpcServiceInnotedClasses.addAll(ClassUtil.getRpcServiceInnotedClasses(packet));
            }
            this.serviceBeanDefinitions.addAll(ServiceBeanRegistry.getRpcServiceDefinitions(RpcServiceInnotedClasses));
            return this;
        } else if(this.clazz.isAnnotationPresent(RpcClient.class)){
            //扫描其下的所有子包中的类，处理所有注解了RpcReference的成员变量。
            RpcClient annotation = this.clazz.getAnnotation(RpcClient.class);
            String[] packets = annotation.ServicePacketScan();
            Set<Class<?>> RpcReferenceInnotedClasses = new HashSet<>();
            for (String packet : packets){
                RpcReferenceInnotedClasses.addAll(ClassUtil.getRpcReferenceInnotedClasses(packet));
            }
            // todo 代理注入的时候需要单例模式
            this.clientBeanDefinitions.addAll(ClientBeanRegistry.getRpcClientDefinitions(RpcReferenceInnotedClasses));
            //todo：如何注入代理？
            return this;
        }
        //todo 异常处理
        throw new RuntimeException();
    }

    public IocContainer run(){
        if(this.clazz.isAnnotationPresent(RpcServer.class)) { //服务端； todo: 优化
            RpcServer anno = this.clazz.getAnnotation(RpcServer.class);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(anno.Host(), anno.Port());
            this.serviceBeanDefinitions.forEach(x -> {
                //本地注册服务；
                LocalServiceRecord.registerService(x.getInterfaceClazz().getSimpleName(), x.getMyClazz());
                //注册服务至nacos：
                //todo：version & group
                ServiceRegistry serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("nacos");
                serviceRegistry.register(x.getInterfaceClazz().getSimpleName(), inetSocketAddress);
            });
            //初始化服务端，建立远端连接。
            NettyServerInit RpcServer = new NettyServerInit(inetSocketAddress);
            RpcServer.run();
        } else if (this.clazz.isAnnotationPresent(RpcClient.class)) {
            ServiceRegistry registry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("nacos");
            //服务发现 + 负载均衡
            List<InetSocketAddress> addresses = registry.lookUpService(ServiceApi.class.getSimpleName());
            RandomLoadBalance loadBalancer = new RandomLoadBalance();//todo: ?
            InetSocketAddress targetAddress = loadBalancer.getService(addresses);//这个负载均衡感觉也就是意思意思（）
            //建立远端连接
            NettyClientInit nettyClient = new NettyClientInit(targetAddress);
            nettyClient.run();
        }
        return this;
    }
}
