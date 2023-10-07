package github.xunolan.rpcproject.definition;

import github.xunolan.rpcproject.annotation.server.RpcService;
import lombok.Data;

@Data
public class RpcServiceDefinition {
    private Class<?> myClazz;
    private Class<?> interfaceClazz;
    private String group;
    private String version;

    public static RpcServiceDefinition fromInnotedClass(Class<?> clazz){
        RpcService anno = clazz.getAnnotation(RpcService.class);
        assert anno != null;
        RpcServiceDefinition rpcServiceDefinition = new RpcServiceDefinition();
        rpcServiceDefinition.setMyClazz(clazz);
        rpcServiceDefinition.setInterfaceClazz(anno.ImplementClazz());
        rpcServiceDefinition.setGroup(anno.Group());
        rpcServiceDefinition.setVersion(anno.Version());
        return rpcServiceDefinition;
    }
}
