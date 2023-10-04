package github.xunolan.rpcproject.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

//包结构有点乱。理论上extension不应当与serializer实现放在一起的。
public class ExtensionLoader<T> {
    //如果对标dubbo，对外接口应当是ExtensionLoader.getExtensionLoader(Interface.class).getExtension("SpecificServiceName");
    //似乎并没有SPI的必要。因为指定了getExtensionLoader接口。这一步类似于jdk的ServiceLoader.load(Interface.class).
    private String URL = "META-INF/services/";
    private Class<T> type;//一个ExtensionLoader只会对应一个classLooader；
    //接口类和对应的ExtensionLoader对象的map。相当于map的map；
    private static Map<Class<?>, ExtensionLoader<?>> INTERFACETYPE_EXTENSIONLOADER_MAP = new HashMap<>();
    //根据接口，自然得出了返回值为ExtensionLoader的结论（
    //不然本来自己还想使用若干个Map来处理的。
    //不过确实通过保存一个name-自己对象的staticMap，结构确实更加清晰。要记住这种方法。
    public static ExtensionLoader<?> getExtensionLoader(Class<?> clazz) {
        ExtensionLoader<?> extensionLoader = INTERFACETYPE_EXTENSIONLOADER_MAP.getOrDefault(clazz, null);
        if(extensionLoader == null) {
            //没有缓存。需要初始化。
            extensionLoader = new ExtensionLoader<>(clazz);
            INTERFACETYPE_EXTENSIONLOADER_MAP.putIfAbsent(clazz, extensionLoader);
        }
        return extensionLoader;
    }
    private Map<String, Class<?>> aliasClassMap = new HashMap<>();
    private ExtensionLoader(Class<T> type) {
        this.type = type;
        //先忽略缓存。全部现场查找并构建。
        //对于接口类，先通过获取type的全类名来定位配置路径下的文件。
        String filename = URL + type.getName();
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(filename);
            if(urls != null) {
                while(urls.hasMoreElements()) {
                    //遍历文件内部的每一行；
                    URL resourceUrl = urls.nextElement();
                    //在遍历的过程中顺便初始化了这个Class接口类的所有别名和具体类。
                    loadResource(resourceUrl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //处理单个配置文件。对于每一行，将服务别名和具体包名全路径放入map中。
    private void loadResource(URL resourceUrl) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), StandardCharsets.UTF_8))){
           String line;
           while((line = reader.readLine()) != null){
               final int ci = line.indexOf("#");
               if(ci >= 0)
                   line = line.substring(0, ci);
               line = line.trim();
               if(line.length() > 0){
                   try {
                       final int ei = line.indexOf('=');
                       String name = line.substring(0, ei).trim();
                       String clazzName = line.substring(ei + 1).trim();
                       // our SPI use key-value pair so both of them must not be empty
                       if (name.length() > 0 && clazzName.length() > 0) {
                           Class<?> clazz = this.getClass().getClassLoader().loadClass(clazzName);
                           aliasClassMap.put(name, clazz);
                       }
                   } catch (ClassNotFoundException e) {
                       e.printStackTrace();
                   }
               }
           }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public T getExtension(String name) {
        Class<?> clazz =  aliasClassMap.get(name);
        try{
            Object instance = clazz.newInstance();
            return this.type.cast(instance);
        } catch(InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
 
}
