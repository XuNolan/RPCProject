package github.xunolan.rpcproject.register;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class LocalServiceRecord {
    private static final Map<String, Class<?>> serviceMap = new HashMap<>();//name-object;
    private static final Map<String, Object> singletonObject = new HashMap<>();
    public static Boolean registerService(String name, Class<?> serviceClass, Object instance){
        if(serviceMap.containsKey(name))
            return false;
        serviceMap.put(name, serviceClass);//api, apiimpl.class
        singletonObject.put(name, instance);//api, apiimpl.instance
        return true;
    }
    public static Class<?> getService(String name){
        if(!serviceMap.containsKey(name))
            return null;
        return serviceMap.get(name);
    }
    public static Object getServiceInstanceByName(String name){
        if(!serviceMap.containsKey(name))
            return null;
        return singletonObject.get(name);
    }
    public static <T> T getServiceInstance(Class<T> clazz){
        if(!singletonObject.containsKey(clazz.getName())){
            return null;
        }
        return clazz.cast(singletonObject.get(clazz.getName()));
    }
}
