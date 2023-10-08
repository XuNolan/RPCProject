package github.xunolan.rpcproject.definition;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BeanDefinition {
    //推测需要使用树形来管理依赖和被依赖的关系，引入变量Depended。若其只是一个单一的Component。不被其他任何人autowired，则其为空。否则将记录被何人Depended。
    //不够。如果按照自顶向下的初始化顺序，应当保存Bean-其内部Autowired的所有成员的这样的List。

    public Class<?> clazz;
    public Map<String, BeanDefinition> autowired = new HashMap<>(); //name-otherComponent
    public Map<String, ClientBeanDefinition> rpcReferenced = new HashMap<>();// name-otherReferenced;

}
