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
        //初始化所有Component的BeanDefinition。记录内部若有Autowired和RpcReference的则记录在BeanDefinition内部待进行依赖注入。
        //规定RpcReference的不会依赖其他Bean。
        for (Class<?> clazz : componentAnnotedSet) {
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.clazz = clazz;
            Method[] methods = clazz.getDeclaredMethods();
            for(Method method : methods){
                if(method.isAnnotationPresent(Autowired.class)){
                    BeanDefinition fieldBeanDefinition = new BeanDefinition();
                    //假设set只会setAutowired的方法；
                    fieldBeanDefinition.clazz = method.getParameterTypes()[0];//自己的clazz
                    fieldBeanDefinition.method = method;
                    beanDefinition.autowired.put(method.getName(), fieldBeanDefinition);//map的key是不是还可以考虑一下。暂时还没啥用。也许之后Qualifier有用。
                }
                if(method.isAnnotationPresent(RpcReference.class)){
                    ProxyBeanDefinition fieldProxyBeanDefinition = ProxyBeanDefinition.fromInnotedClassAndField(clazz, method);
                    beanDefinition.rpcReferenced.put(method.getName(), fieldProxyBeanDefinition);
                }
            }
            beanDefinitions.add(beanDefinition);
        }


    }



}
