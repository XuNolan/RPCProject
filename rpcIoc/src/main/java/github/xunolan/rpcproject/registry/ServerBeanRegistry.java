package github.xunolan.rpcproject.registry;

import github.xunolan.rpcproject.annotation.server.RpcService;
import github.xunolan.rpcproject.definition.BeanDefinition;
import github.xunolan.rpcproject.utils.ClassUtil;

import java.util.HashSet;
import java.util.Set;

public class ServerBeanRegistry extends BeanRegistry {
    public Set<Class<?>> RpcServiceAnnotedClass = new HashSet<>();
    @Override
    public void registerBean(String[] packages) {
        for(String pkg : packages) {
            RpcServiceAnnotedClass.addAll(ClassUtil.getInnotedClasses(pkg, RpcService.class));
        }
        for (Class<?> clazz : RpcServiceAnnotedClass) {
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.clazz = clazz;
            //todo： 根据之后实例化过程需要的数据再说；
            beanDefinitions.add(beanDefinition);
        }
    }
}
