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
}
