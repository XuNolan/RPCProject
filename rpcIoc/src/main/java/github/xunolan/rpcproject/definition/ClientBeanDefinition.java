package github.xunolan.rpcproject.definition;

import github.xunolan.rpcproject.annotation.client.RpcReference;
import lombok.Data;

import java.lang.reflect.Field;

@Data
public class ClientBeanDefinition {
    private Class<?> fatherClazz;
    private Class<?> implementedService;
    private Field field;
    private String group;
    private String version;
    public static ClientBeanDefinition fromInnotedClassAndField(Class<?> clazz, Field field){
        ClientBeanDefinition clientBeanDefinition = new ClientBeanDefinition();
        RpcReference anno = field.getAnnotation(RpcReference.class);
        clientBeanDefinition.fatherClazz = clazz;
        clientBeanDefinition.field = field;
        clientBeanDefinition.group = anno.Gruop();
        clientBeanDefinition.version = anno.Version();
        return clientBeanDefinition;
    }
}
