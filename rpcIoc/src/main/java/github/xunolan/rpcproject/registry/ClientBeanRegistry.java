package github.xunolan.rpcproject.registry;

import github.xunolan.rpcproject.definition.ClientBeanDefinition;
import github.xunolan.rpcproject.annotation.client.RpcReference;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class ClientBeanRegistry {
    public static Set<ClientBeanDefinition> getRpcClientDefinitions(Set<Class<?>> classList) {
        Set<ClientBeanDefinition> set = new HashSet<>();
        for(Class<?> clazz : classList){
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields) {
                //todo： 每个注解了的成员变量对应一个ClientBeanDefinition
                //还需要一个类来负责缓存和实例化
                if(field.isAnnotationPresent(RpcReference.class)){
                    set.add(ClientBeanDefinition.fromInnotedClassAndField(clazz,field));
                }
            }
        }
        return set;
    }
}
