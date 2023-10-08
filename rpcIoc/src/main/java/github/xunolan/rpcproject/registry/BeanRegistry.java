package github.xunolan.rpcproject.registry;

import github.xunolan.rpcproject.annotation.Autowired;
import github.xunolan.rpcproject.annotation.Component;
import github.xunolan.rpcproject.annotation.client.RpcReference;
import github.xunolan.rpcproject.annotation.server.RpcService;
import github.xunolan.rpcproject.definition.BeanDefinition;
import github.xunolan.rpcproject.definition.ClientBeanDefinition;
import github.xunolan.rpcproject.definition.ServiceBeanDefinition;
import github.xunolan.rpcproject.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BeanRegistry {
    //todo：理论上应当service归service、Reference归Reference。相互不可见，IOC各自保存。
    private static Set<Class<?>> componentAnnotedSet = new HashSet<>();
    private static Set<Class<?>> rpcServiceAnnotedSet = new HashSet<>();
    private static Set<BeanDefinition> beanDefinitions = new HashSet<>();
    private static Set<ServiceBeanDefinition> serviceBeanDefinitions = new HashSet<>();

    public static void scanAnnotations(String[] packages){ //扫描要被IOC容器进行管理的所有类。
        //1. 定位所有Component和RpcService注解；（RpcReference归于Autowired一类）
        for(String pkg : packages){
            componentAnnotedSet.addAll(getInnotedClasses(pkg, Component.class));
            rpcServiceAnnotedSet.addAll(getInnotedClasses(pkg, RpcService.class));
        }
        //2. 初始化所有BeanDefinition
        for(Class<?> clazz : componentAnnotedSet) {
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
        //todo: service注解不存在依赖问题？暂时遵循此前的逻辑。
        serviceBeanDefinitions.addAll(rpcServiceAnnotedSet.stream().map(ServiceBeanDefinition::fromInnotedClass).collect(Collectors.toSet()));
    }

    public static Set<Class<?>> getInnotedClasses(String packageName, Class<? extends Annotation> clazz){
        return ClassUtil.getAllClasses(packageName).stream().filter(x -> {
            Annotation annotation = x.getAnnotation(clazz);
            return annotation!=null;
        }).collect(Collectors.toSet());
    }

}
