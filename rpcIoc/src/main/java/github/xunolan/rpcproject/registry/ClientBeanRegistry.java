package github.xunolan.rpcproject.registry;

import github.xunolan.rpcproject.annotation.Autowired;
import github.xunolan.rpcproject.annotation.Component;
import github.xunolan.rpcproject.annotation.client.RpcReference;
import github.xunolan.rpcproject.definition.BeanDefinition;
import github.xunolan.rpcproject.definition.ProxyBeanDefinition;
import github.xunolan.rpcproject.utils.ClassUtil;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ClientBeanRegistry extends BeanRegistry{
    private final Set<Class<?>> componentAnnotedSet = new HashSet<>();

    @Override
    public void registerBean(String[] packages) {
        //1. 定位所有Component注解，没有标记Component的就不管了。
        for(String pkg : packages) {
            componentAnnotedSet.addAll(ClassUtil.getInnotedClasses(pkg, Component.class));
        }
        //初始化所有Component的BeanDefinition。
        //规定RpcReference的不会依赖其他Bean。
        //内部是有Autowired还是RpcService的留待ClientBeanFactory进行扫描。
        for (Class<?> clazz : componentAnnotedSet) {
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.clazz = clazz;
            //todo： 根据之后实例化过程需要的数据再说；
            beanDefinitions.add(beanDefinition);
        }
    }
}
