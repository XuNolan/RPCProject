package github.xunolan.rpcproject.factory;

import github.xunolan.rpcproject.definition.BeanDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClientBeanFactory extends BeanFactory {
    //本Factory缓存所有已构造好的Bean；
    //需要设计一下缓存的结构；
    //一级缓存
    private static Map<String, Object> singletonObject = new HashMap<>();//key暂时使用BeanName？BeanName是什么？全类名？
    //二级缓存。缓存原始对象？
    private static Map<String, Object> earlySingletonObject = new HashMap<>();


    public ClientBeanFactory(Set<BeanDefinition> beanDefinitions) {
        super(beanDefinitions);
    }

    public void beanInitialize() {
        for (BeanDefinition beanDefinition : super.beanDefinitions.values()) {
            singletonObject.put(beanDefinition.clazz.getName(), getBean(beanDefinition.clazz));

        }
    }

    public <T> T getBean(Class<T> clazz) {
        //构造原始对象
        BeanDefinition beanDefinition = super.beanDefinitions.get(clazz);
        try {
            Object object = beanDefinition.clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e){
            e.printStackTrace();
        }
        //放入二级缓存
        //依赖注入
        //初始化（？）

    }
}



