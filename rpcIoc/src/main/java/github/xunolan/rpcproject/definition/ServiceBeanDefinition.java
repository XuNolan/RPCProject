package github.xunolan.rpcproject.definition;

import github.xunolan.rpcproject.annotation.server.RpcService;
import lombok.Data;

@Data
public class ServiceBeanDefinition {
    private Class<?> myClazz;
    private Class<?> interfaceClazz;
    private String group;
    private String version;

    public static ServiceBeanDefinition fromInnotedClass(Class<?> clazz){
        RpcService anno = clazz.getAnnotation(RpcService.class);
        assert anno != null;
        ServiceBeanDefinition serviceBeanDefinition = new ServiceBeanDefinition();
        serviceBeanDefinition.setMyClazz(clazz);
        serviceBeanDefinition.setInterfaceClazz(anno.ImplementClazz());
        serviceBeanDefinition.setGroup(anno.Group());
        serviceBeanDefinition.setVersion(anno.Version());
        return serviceBeanDefinition;
    }
}
