package github.xunolan.rpcproject.definition;

import java.util.HashMap;
import java.util.Map;


public class BeanDefinition {
    //需要添加构造函数和对应变量。如何选择构造函数、构造实例的值？
    public Class<?> clazz;
    public Map<String, BeanDefinition> autowired = new HashMap<>(); //name-otherComponent
    public Map<String, ClientBeanDefinition> rpcReferenced = new HashMap<>();// name-RPCReferenced;

}
