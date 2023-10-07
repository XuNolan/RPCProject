package github.xunolan.rpcproject.registry;

import github.xunolan.rpcproject.definition.RpcServiceDefinition;

import java.util.Set;
import java.util.stream.Collectors;

public class BeanRegistry {
    public static Set<RpcServiceDefinition> getRpcServiceDefinitions(Set<Class<?>> classList){//扫描RpcServer注解。
        return classList.stream().map(RpcServiceDefinition::fromInnotedClass).collect(Collectors.toSet());
    }

}
