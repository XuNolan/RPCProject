package github.xunolan.rpcproject.registry;

import github.xunolan.rpcproject.annotation.Autowired;
import github.xunolan.rpcproject.annotation.Component;
import github.xunolan.rpcproject.annotation.client.RpcReference;
import github.xunolan.rpcproject.definition.BeanDefinition;
import github.xunolan.rpcproject.definition.ClientBeanDefinition;
import github.xunolan.rpcproject.utils.ClassUtil;

import java.lang.reflect.Field;
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
            Field[] fields = clazz.getFields();
            for(Field field : fields){
                if(field.isAnnotationPresent(Autowired.class)){
                    BeanDefinition fieldBeanDefinition = new BeanDefinition();
                    fieldBeanDefinition.clazz = field.getDeclaringClass();
                    beanDefinition.autowired.put(field.getName(), fieldBeanDefinition);
                }
                if(field.isAnnotationPresent(RpcReference.class)){
                    ClientBeanDefinition fieldClientBeanDefinition = ClientBeanDefinition.fromInnotedClassAndField(clazz, field);
                    beanDefinition.rpcReferenced.put(field.getName(), fieldClientBeanDefinition);
                }
            }
            beanDefinitions.add(beanDefinition);
        }


    }



}
