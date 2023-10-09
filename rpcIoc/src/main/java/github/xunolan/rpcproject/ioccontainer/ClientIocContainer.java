package github.xunolan.rpcproject.ioccontainer;

import github.xunolan.rpcproject.definition.BeanDefinition;
import github.xunolan.rpcproject.factory.ClientBeanFactory;
import github.xunolan.rpcproject.registry.ClientBeanRegistry;

import java.util.Set;

public class ClientIocContainer extends IocContainer {

    public ClientIocContainer(Class<?> clazz) {
        super(clazz);
    }

    @Override
    void initBeanRegistry() {
        super.beanRegistry = new ClientBeanRegistry(); //引入基类的目的是将client和Server的BeanRegistry与BeanDefinition都区分开，对IOC基类不可见；
        //beanRegistry.registerBean(scanPackages);
    }

    @Override
    void initBeanFactory() {
        super.beanFactory = new ClientBeanFactory();
        //beanFactory.beanInitialize();
    }

}
