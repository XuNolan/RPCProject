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
import github.xunolan.rpcproject.registry.ServiceBeanRegistry;
import github.xunolan.rpcproject.registry.ServiceRegistry;
import github.xunolan.rpcproject.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class IocContainer {
    /*
    * 三步：
    * 1. 扫描所有注解。扫描范围由创建IOC容器的类指定，扫描内容包括所有注解。
    * 1.1 先处理iocContainer所在类上的注解PackageScan。todo：PackageScan单独提取为一个注解。
    * 1.2 先处理扫描范围下的所有Component和Autowired，对于RpcService和RpcReference，将其作为Component和Autowired的子集进行处理。
    *
    * 2. 将上述扫描得到的类全部转换为BeanDefinition，并保存在BeanFactory内部？这一部分逻辑由BeanRegistry完成。
    * 3. 在BeanFactory中，对RpcService 创建单例缓存；对RpcReference，进行代理的实例化。将其他的BeanDefinition实例化为Bean。完成依赖注入等工作。
    *ean
     * 1.3 最后处理和初始化RpcServer和RpcClient，进行服务端和客户端的netty初始化以及连接建立。进入加载流程；
    * */

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
            List<InetSocketAddress> addresses = registry.lookUpService(ServiceApi.class.getSimpleName());
            InetSocketAddress targetAddress = loadBalancer.getService(addresses);//这个负载均衡感觉也就是意思意思（）
            //建立远端连接
            NettyClientInit nettyClient = new NettyClientInit(targetAddress);
            nettyClient.run();

        }else if(this instanceof ServerIocContainer){
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
            if(this.clazz.isAnnotationPresent(RpcService.class)){
                RpcServer anno = this.clazz.getAnnotation(RpcServer.class);
                inetSocketAddress = new InetSocketAddress(anno.Host(), anno.Port());
                //todo 服务端的ioc逻辑

            }
            NettyServerInit RpcServer = new NettyServerInit(inetSocketAddress);
            RpcServer.run();
        }
        return this;
    }

    abstract void initBeanRegistry(String[] scanPackages);
    abstract void initBeanFactory(Set<BeanDefinition> beanDefinitions);


//    public IocContainer run(){
//        if(this.clazz.isAnnotationPresent(RpcServer.class)) { //服务端； todo: 优化
//
//            this.serviceBeanDefinitions.forEach(x -> {
//                //本地注册服务；
//                LocalServiceRecord.registerService(x.getInterfaceClazz().getSimpleName(), x.getMyClazz());
//                //注册服务至nacos：
//                //todo：version & group
//                ServiceRegistry serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("nacos");
//                serviceRegistry.register(x.getInterfaceClazz().getSimpleName(), inetSocketAddress);
//            });
//            //初始化服务端，建立远端连接。
//
//        } else if (this.clazz.isAnnotationPresent(RpcClient.class)) {
//            ServiceRegistry registry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("nacos");
//            //服务发现 + 负载均衡
//            List<InetSocketAddress> addresses = registry.lookUpService(ServiceApi.class.getSimpleName());
//            RandomLoadBalance loadBalancer = new RandomLoadBalance();//todo: ?
//            InetSocketAddress targetAddress = loadBalancer.getService(addresses);//这个负载均衡感觉也就是意思意思（）
//            //建立远端连接
//            NettyClientInit nettyClient = new NettyClientInit(targetAddress);
//            nettyClient.run();
//        }
//        return this;
//    }



}
