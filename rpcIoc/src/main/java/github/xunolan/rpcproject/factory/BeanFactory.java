package github.xunolan.rpcproject.factory;

import github.xunolan.rpcproject.definition.BeanDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BeanFactory {
    public Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();
    //一级缓存
    public static Map<String, Object> singletonObject = new HashMap<>();//key暂时使用BeanName？BeanName是什么？全类名
    //二级缓存。缓存原始对象？暂且不考虑循环依赖。
    //private static Map<String, Object> earlySingletonObject = new HashMap<>();
    public abstract void beanInitialize(Set<BeanDefinition> beanDefinitions);
    public Object getBean(String beanName){
        return singletonObject.get(beanName);
    }

}
