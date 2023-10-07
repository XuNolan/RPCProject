package github.xunolan.rpcproject;

import github.xunolan.rpcproject.annotation.client.RpcClient;
import github.xunolan.rpcproject.annotation.server.RpcServer;
import github.xunolan.rpcproject.api.ServiceApi;
import github.xunolan.rpcproject.definition.RpcServiceDefinition;
import github.xunolan.rpcproject.extension.ExtensionLoader;
import github.xunolan.rpcproject.netty.NettyServerInit;
import github.xunolan.rpcproject.register.LocalServiceRecord;
import github.xunolan.rpcproject.registry.BeanRegistry;
import github.xunolan.rpcproject.registry.ServiceRegistry;
import github.xunolan.rpcproject.utils.ClassUtil;


import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class IocContainer {
    private Set<RpcServiceDefinition> rpcServiceDefinitions = new HashSet<>();
    //todo:ioc容器作为父类，引入serverIoc和clientIoc
    private Class<?> clazz;
    public IocContainer(Class<?> clazz){
        this.clazz = clazz;
    }
    public IocContainer initIocContainer(){
        Annotation annotation = this.clazz.getAnnotation(RpcServer.class);
        if(annotation != null) { //服务端；
            //获得待扫描的包。扫描其包下的所有子包中有携带RpcService的类，将其转换为BeanDefinition并保存在IOC容器中。
            //扫描结束后统一实例化？beanMap有什么保存的必要吗？在原来的SpringIOC中，主要是由于存在依赖注入，所以不能直接实例化对象，只能在扫描的时候保存BeanDefinition。
            //这里应当是不会存在依赖的。毕竟是rpc。
            String[] Packets = ((RpcServer) annotation).ServicePacketScan();
            //扫描其下的所有子包，处理所有注解了RpcService的类。
            Set<Class<?>> classes = new HashSet<>();
            for (String packet : Packets){
                classes.addAll(ClassUtil.getRpcServiceInnoClasses(packet));
            }
            this.rpcServiceDefinitions.addAll(BeanRegistry.getRpcServiceDefinitions(classes));
            return this;
        } else if((annotation = clazz.getAnnotation(RpcClient.class))!= null){

            //todo： 客户端的IOC init；
            return this;
        }
        //todo 异常处理
        throw new RuntimeException();
    }

    public IocContainer run(){ //
        Annotation annotation = this.clazz.getAnnotation(RpcServer.class);
        if(annotation != null) { //服务端； todo: 优化
            RpcServer anno = (RpcServer) annotation;
            InetSocketAddress inetSocketAddress = new InetSocketAddress(anno.Host(), anno.Port());
            this.rpcServiceDefinitions.forEach( x -> {
                //本地注册服务；
                LocalServiceRecord.registerService(x.getInterfaceClazz().getSimpleName(), x.getMyClazz());
                //注册服务至nacos：
                //todo：version & group
                ServiceRegistry serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("nacos");
                serviceRegistry.register(x.getInterfaceClazz().getSimpleName(), inetSocketAddress);
            });
            //初始化客户端；
            NettyServerInit RpcServer = new NettyServerInit(inetSocketAddress);
            RpcServer.run();
        }
        //todo： 客户端；
        return this;
    }
}
