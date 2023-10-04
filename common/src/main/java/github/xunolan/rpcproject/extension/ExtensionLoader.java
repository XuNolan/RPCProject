package github.xunolan.rpcproject.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class ExtensionLoader<T> {
    private static final String URL = "META-INF/services/";
    private static final Map<Class<?>, ExtensionLoader<?>> INTERFACETYPE_EXTENSIONLOADER_MAP = new HashMap<>();

    private final Class<T> type;

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> clazz) {
        //进行校验，并优化泛型；泛型优化主要是为了与ExtensionLoader类的泛型保持一致。
        if(clazz == null){
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if(!clazz.isInterface()){
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        if(clazz.getAnnotation(SPI.class) == null){
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) INTERFACETYPE_EXTENSIONLOADER_MAP.get(clazz);
        if(extensionLoader == null) {
            extensionLoader = new ExtensionLoader<>(clazz);
            INTERFACETYPE_EXTENSIONLOADER_MAP.putIfAbsent(clazz, extensionLoader);
        }
        return extensionLoader;
    }

    private ExtensionLoader(Class<T> type) {
        this.type = type;
    }
    //两个问题：
    //nameInstanceCacheMap和classInstanceMap是否重复？之前示例代码中，classInstanceMap是static的。
    //holder还是没有引入。有什么用吗？
    private final Map<String, Object> nameInstanceCacheMap = new HashMap<>();
    private final Map<String, Class<?>> nameClassCacheMap = new HashMap<>();
    private final Map<Class<?>, Object> classInstanceMap = new HashMap<>();
    public T getExtension(String name) {
        //缓存处理1。对Extension实例进行缓存；
        Object extensionInstance = nameInstanceCacheMap.get(name);
        if(extensionInstance == null) {
//            //缓存为空，考虑进行加载。走获取class的路线。
//            //下述是原无缓存2的逻辑
//            loadDirectory();
//            Class<?> clazz =  aliasClassMap.get(name);
//            try{
//                extensionCachedInstance = clazz.newInstance();
//                nameInstanceCacheMap.putIfAbsent(name,extensionInstance);//cache
//                return this.type.cast(extensionInstance);
//            } catch(InstantiationException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
            //缓存处理2。对Extension实例的class对象进行缓存。
            Class<?> clazz = nameClassCacheMap.get(name);
            if(clazz == null) {
                loadDirectory();//从文件夹内部加载配置的服务的类class
                clazz = nameClassCacheMap.get(name);//重新获取；
            }
            assert clazz != null;
            try {
                //缓存处理3。对Extension实例的实例化对象进行缓存。
                extensionInstance = classInstanceMap.get(clazz);
                if(extensionInstance == null){
                    extensionInstance = clazz.newInstance();
                    classInstanceMap.putIfAbsent(clazz, extensionInstance);//cache
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            nameInstanceCacheMap.putIfAbsent(name,extensionInstance);
        }
        return this.type.cast(extensionInstance);
    }

    private void loadDirectory(){
        //寻找META文件夹内部接口类对应的文件，并加载别名和class的逻辑单独提出；
        String filename = URL + type.getName();
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(filename);
            if(urls != null) {
                while(urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(resourceUrl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
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
                       if (name.length() > 0 && clazzName.length() > 0) {
                           Class<?> clazz = this.getClass().getClassLoader().loadClass(clazzName);
                           nameClassCacheMap.put(name, clazz);
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

}
