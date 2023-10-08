package github.xunolan.rpcproject.ioccontainer;

import github.xunolan.rpcproject.definition.BeanDefinition;

import java.util.Set;

public class ServerIocContainer extends IocContainer{
    //对于服务端的ioc，（暂时）只需要处理RpcServer，并提供单例缓存池服务；
    public ServerIocContainer(Class<?> clazz) {
        super(clazz);
    }

    @Override
    void initBeanRegistry(String[] scanPackages) {
    }

    @Override
    void initBeanFactory(Set<BeanDefinition> beanDefinitions) {
    }

}
