package github.xunolan.rpcproject.factory;

import github.xunolan.rpcproject.annotation.Autowired;
import github.xunolan.rpcproject.annotation.client.RpcReference;
import github.xunolan.rpcproject.definition.BeanDefinition;
import github.xunolan.rpcproject.netty.NettyClientInit;
import github.xunolan.rpcproject.proxy.ProxyFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class ClientBeanFactory extends BeanFactory {

    public void beanInitialize(Set<BeanDefinition> beanDefinitions) {
        beanDefinitions.stream().forEach( x -> this.beanDefinitions.put(x.clazz, x));
        for (BeanDefinition beanDefinition : super.beanDefinitions.values()) {
            singletonObject.putIfAbsent(beanDefinition.clazz.getName(), getBean(beanDefinition.clazz, beanDefinition.clazz.getName()));
        }
    }

    public <T> T getBean(Class<T> clazz, String beanName) { //beanName暂且使用全类名。todo：有没有问题？
        if(singletonObject.containsKey(beanName)){
            // 缓存中已有，直接返回。
            // 其实对于最外层的Component注解的Bean，这里绝对不会命中。
            // 只是为了在依赖注入过程中给注入的依赖使用的单例。
            return clazz.cast(singletonObject.get(beanName));
        }
        //构造原始对象
        BeanDefinition beanDefinition = super.beanDefinitions.get(clazz);
        try {
            Object object = beanDefinition.clazz.getConstructor().newInstance();//默认有无参构造器
            Field[] fields = beanDefinition.clazz.getFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Autowired.class)) {
                    Object dep = getBean(field.getType(), field.getType().getName());//获取普通对象
                    //将这个依赖注入进去；
                    field.set(object, dep);
                } else if(field.isAnnotationPresent(RpcReference.class)) {
                    T rpcProxy = new ProxyFactory<T>().getProxy((Class<T>) field.getType());//调用ProxyFactory获取代理对象。
                    field.set(object, rpcProxy);
                }
            }
            return clazz.cast(object);
            //todo : 初始化（？）不确定怎么处理基本变量的初始化。调用构造函数？
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}



