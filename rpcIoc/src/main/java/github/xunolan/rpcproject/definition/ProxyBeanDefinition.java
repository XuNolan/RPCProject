package github.xunolan.rpcproject.definition;

import github.xunolan.rpcproject.annotation.client.RpcReference;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Data
public class ProxyBeanDefinition {
    //需代理的接口类-ClientBeanDefinition的map
    public static Map<Class<?>, ProxyBeanDefinition> IMPSERVICE_INFO_MAP = new HashMap<>();
    private Class<?> fatherClazz;
    private Class<?> implementedService;
    private Method method;
    private String group;
    private String version;
    public static ProxyBeanDefinition fromInnotedClassAndField(Class<?> clazz, Method method){
        ProxyBeanDefinition proxyBeanDefinition = new ProxyBeanDefinition();
        RpcReference anno = method.getAnnotation(RpcReference.class);
        //这俩似乎没用；todo
        proxyBeanDefinition.fatherClazz = clazz;
        proxyBeanDefinition.method = method;

        proxyBeanDefinition.implementedService = anno.ImpService();
        proxyBeanDefinition.group = anno.Gruop();
        proxyBeanDefinition.version = anno.Version();
        IMPSERVICE_INFO_MAP.putIfAbsent(proxyBeanDefinition.implementedService, proxyBeanDefinition);
        return proxyBeanDefinition;
    }
}
