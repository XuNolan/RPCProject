package github.xunolan.rpcproject.definition;

import github.xunolan.rpcproject.annotation.client.RpcReference;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
public class ClientBeanDefinition {
    //需代理的接口类-ClientBeanDefinition的map
    public static Map<Class<?>, ClientBeanDefinition> IMPSERVICE_INFO_MAP = new HashMap<>();
    private Class<?> fatherClazz;
    private Class<?> implementedService;
    private Field field;
    private String group;
    private String version;
    public static ClientBeanDefinition fromInnotedClassAndField(Class<?> clazz, Field field){
        ClientBeanDefinition clientBeanDefinition = new ClientBeanDefinition();
        RpcReference anno = field.getAnnotation(RpcReference.class);
        //这俩似乎没用；todo
        clientBeanDefinition.fatherClazz = clazz;
        clientBeanDefinition.field = field;

        clientBeanDefinition.implementedService = anno.ImpService();
        clientBeanDefinition.group = anno.Gruop();
        clientBeanDefinition.version = anno.Version();
        IMPSERVICE_INFO_MAP.putIfAbsent(clientBeanDefinition.implementedService, clientBeanDefinition);
        return clientBeanDefinition;
    }
}
