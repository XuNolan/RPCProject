package github.xunolan.rpcproject.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ExtensionLoader<T> {
    //两个问题：
    //nameInstanceCacheMap和classInstanceMap是否重复？之前示例代码中，classInstanceMap是static的。
    //holder还是没有引入。有什么用吗？
    //新增：所有的上述优化都是为了单例模式下的双重锁校验服务的。holder也是如此。
    //新增问题：如何理解非static的需要双重锁校验，而static的直接putIfAbsent+重新获取即可
    //这里有一个误区。通常来说，单例模式中的单例需要为static，保证对象属于类而不是对象（保证单例）。这里不需要static是因为使用Map保证了增改可以唯一定位那个对象。
    // 所以，不是"非static的需要双重锁校验"，而是为什么两个static Map的不需要双重锁校验
    //static Map不需要双重锁是因为，重复加载也没有关系？因为代价比较小，而且加载（初始化的）只是原始类。
    //第一个static Map加载的是原始的ExtensionLoader对象；第二个是反射加载的Object；这些都是"只要其中一个可用就可以了，多加载一次也没关系"，也就是不需要保证单例；
    //不知道这样理解对不对；
    private static final String URL = "META-INF/services/";
    private static final Map<Class<?>, ExtensionLoader<?>> INTERFACETYPE_EXTENSIONLOADER_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCE_MAP = new ConcurrentHashMap<>();
    private final Class<T> type;
    private final Map<String, Holder<Object>> nameInstanceCacheMap = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> nameClassCacheMap = new Holder<>();

    private ExtensionLoader(Class<T> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> clazz) {
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
            //必须putIfAbsent。这里没有加锁。有可能另一线程抢先初始化了。
            INTERFACETYPE_EXTENSIONLOADER_MAP.putIfAbsent(clazz, new ExtensionLoader<>(clazz));
            //以及，也需要重新获取一次。否则稍后一些的线程返回的引用将与map中的引用不一致。
            extensionLoader = (ExtensionLoader<S>) INTERFACETYPE_EXTENSIONLOADER_MAP.get(clazz);
        }
        return extensionLoader;
    }

    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        //缓存处理1。对Extension实例进行缓存；需要控制Extension实例为单例；
        Holder<Object> holder = nameInstanceCacheMap.get(name);
        if (holder == null) {
            //因为没有加锁，所以还是putIfAbsent而且先放再取；
            nameInstanceCacheMap.putIfAbsent(name, new Holder<>());
            holder = nameInstanceCacheMap.get(name);
        }
        //想清楚，这里的双重锁校验是保证并发场景下放入holder中的的extensionInstance实例是单例的。
        //理论上需要锁在extensionInstance所属的类。这个类应当是T。但是不太能锁的样子，也不能锁对象（还没初始化呢）
        //锁的根源是防止多个线程进入同步代码块，即保证第二次null校验的大括号内部代码只有一个线程可进入。也就是能锁住就行。
        //双重锁校验的示例代码是构造对象实例。因此使用对象类作为锁。
        //这里是直接使用holder，多个线程往holder里面填东西的时候，操作的是放在map里面的同一个holder对象。因此使用holder作为锁是可以的。
        Object extensionInstance = holder.get();
        if (extensionInstance == null) {
            synchronized (holder) {
                extensionInstance = holder.get();
                if (extensionInstance == null) {
                    extensionInstance = createExtension(name);
                    holder.set(extensionInstance);
                }
            }
        }
        return (T) extensionInstance;
    }
    private T createExtension(String name){
        //缓存处理2 对Extension实例的class对象进行缓存。
        // 一般初次加载会把接口对应的所有备选类的class全部加进来。因此需要控制name-class对象整个为单例；
        Map<String, Class<?>> clazzes = nameClassCacheMap.get();
        if(clazzes == null){
            synchronized (nameClassCacheMap){
                clazzes = nameClassCacheMap.get();
                if(clazzes == null){
                    clazzes = new HashMap<>();
                    loadDirectory(clazzes);
                    nameClassCacheMap.set(clazzes);
                }
            }
        }
        //缓存处理3 对Extension实例的实例化对象进行缓存。
        Class<?> clazz = clazzes.get(name);
        T instance = (T) EXTENSION_INSTANCE_MAP.get(clazz);
        if(instance == null) {
            try {
                EXTENSION_INSTANCE_MAP.putIfAbsent(clazz, clazz.newInstance());//cache
                instance = (T) EXTENSION_INSTANCE_MAP.get(clazz);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses){
        //寻找META文件夹内部接口类对应的文件，并加载别名和class的逻辑单独提出；
        String filename = URL + type.getName();
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(filename);
            if(urls != null) {
                while(urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(extensionClasses, resourceUrl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadResource(Map<String, Class<?>> extensionClasses, URL resourceUrl) {
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
                           extensionClasses.put(name, clazz);
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
