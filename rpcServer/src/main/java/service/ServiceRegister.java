package service;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class ServiceRegister {
    private static Map<String, Class> serviceMap = new HashMap<>();//name-object;
    public static Boolean registerService(String name, Class serviceClass){
        if(serviceMap.containsKey(name))
            return false;
        serviceMap.put(name, serviceClass);
        return true;
    }
    public static Class getService(String name){
        if(!serviceMap.containsKey(name))
            return null;
        return serviceMap.get(name);
    }

}
