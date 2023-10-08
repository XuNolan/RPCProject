package github.xunolan.rpcproject.registry;

import github.xunolan.rpcproject.definition.ServiceBeanDefinition;

import java.util.Set;
import java.util.stream.Collectors;

public class ServiceBeanRegistry {
    public static Set<ServiceBeanDefinition> getRpcServiceDefinitions(Set<Class<?>> classList){//扫描RpcServer注解。
        return classList.stream().map(ServiceBeanDefinition::fromInnotedClass).collect(Collectors.toSet());
    }

}
