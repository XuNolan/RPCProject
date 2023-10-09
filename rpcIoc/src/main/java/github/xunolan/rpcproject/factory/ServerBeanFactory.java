package github.xunolan.rpcproject.factory;

import github.xunolan.rpcproject.annotation.server.RpcService;
import github.xunolan.rpcproject.definition.BeanDefinition;
import github.xunolan.rpcproject.register.LocalServiceRecord;

import java.util.Set;

public class ServerBeanFactory extends BeanFactory{
    //这里解决的是server的单例bean工厂；也就是说，仅处理RpcService。
    @Override
    public void beanInitialize(Set<BeanDefinition> beanDefinitions) {
        beanDefinitions.stream().forEach( x -> this.beanDefinitions.put(x.clazz, x));
        for (BeanDefinition beanDefinition : super.beanDefinitions.values()) {
            Object instance = getBean(beanDefinition.clazz, beanDefinition.clazz.getName());
            Class<?> impService = beanDefinition.clazz.getAnnotation(RpcService.class).ImplementClazz();
            singletonObject.put(impService.getName(), instance);
            LocalServiceRecord.registerService(impService.getName(), beanDefinition.clazz, instance);
        }
        //将实例化的服务注册到本地
        //是不是可以将singletonObject和LocalServiceRecord合并？
        //不太好。IocContainer逻辑上是位于nettyServer更高层的对象，或者将singletonObject变成static的呢？出现循环依赖。

    }

    public <T> T getBean(Class<T> clazz, String beanName){
        if(singletonObject.containsKey(beanName)){
            return clazz.cast(singletonObject.get(beanName));
        }
        //构造RpcService单例。这里暂且视其内部无其他依赖；因此依次实例化即可。
        try{
            Object object = clazz.newInstance();
            return clazz.cast(object);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getServiceClass(String name){
        if(!super.beanDefinitions.containsKey(name)){
            return null;
        }
        return super.beanDefinitions.get(name).clazz;
    }

    public <T> T getServiceInstance(Class<T> clazz){
        if(!singletonObject.containsKey(clazz.getName())){
            return null;
        }
        return clazz.cast(singletonObject.get(clazz.getName()));
    }

}
