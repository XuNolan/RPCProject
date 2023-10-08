package github.xunolan.rpcproject.registry;

import github.xunolan.rpcproject.definition.BeanDefinition;

import java.util.HashSet;
import java.util.Set;

public abstract class BeanRegistry {
    protected Set<BeanDefinition> beanDefinitions = new HashSet<>();
    public abstract void registerBean(String[] packages);
    public Set<BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }
}
