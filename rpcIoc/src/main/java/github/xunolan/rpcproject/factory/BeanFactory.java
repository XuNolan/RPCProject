package github.xunolan.rpcproject.factory;

import github.xunolan.rpcproject.definition.BeanDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BeanFactory {
    public Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();
    public BeanFactory(Set<BeanDefinition> beanDefinitions){
        beanDefinitions.stream().forEach( x -> this.beanDefinitions.put(x.clazz, x));
    }

    //一级缓存
    protected static Map<String, Object> singletonObject = new HashMap<>();//key暂时使用BeanName？BeanName是什么？全类名
    //二级缓存。缓存原始对象？暂且不考虑循环依赖。
    //private static Map<String, Object> earlySingletonObject = new HashMap<>();
    abstract public void beanInitialize();

}
