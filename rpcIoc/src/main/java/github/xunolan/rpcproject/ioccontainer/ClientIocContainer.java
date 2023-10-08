package github.xunolan.rpcproject.ioccontainer;

import github.xunolan.rpcproject.annotation.Autowired;
import github.xunolan.rpcproject.annotation.Component;
import github.xunolan.rpcproject.annotation.client.RpcReference;
import github.xunolan.rpcproject.definition.BeanDefinition;
import github.xunolan.rpcproject.definition.ClientBeanDefinition;
import github.xunolan.rpcproject.factory.BeanFactory;
import github.xunolan.rpcproject.factory.ClientBeanFactory;
import github.xunolan.rpcproject.registry.BeanRegistry;
import github.xunolan.rpcproject.registry.ClientBeanRegistry;
import github.xunolan.rpcproject.utils.ClassUtil;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class ClientIocContainer extends IocContainer {

    public ClientIocContainer(Class<?> clazz) {
        super(clazz);
    }

    @Override
    void initBeanRegistry(String[] scanPackages) {
        super.beanRegistry = new ClientBeanRegistry(); //引入基类的目的是将client和Server的BeanRegistry与BeanDefinition都区分开，对IOC基类不可见；
        beanRegistry.registerBean(scanPackages);
    }

    @Override
    void initBeanFactory(Set<BeanDefinition> beanDefinitions) {
        super.beanFactory = new ClientBeanFactory(beanDefinitions);

    }

}
