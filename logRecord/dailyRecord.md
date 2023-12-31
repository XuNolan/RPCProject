本文档用于记录在实现过程中的一些想法和建议，当天日记和杂谈，进行当天学习内容和debug的总结。

9.25 周一。开始实现rpc框架。途中debug了以下问题：
- netty的责任链模式如何整合到项目流程内部；
- netty的粘包需要自行解决。参照guide-rpc框架的机制，在发送时先发送本包长度再发送实际数据，解包时根据长度从缓冲区中读取。
- netty的bug：客户端和服务端启动之后需要防止其直接退出。对于closeFuture要么使用sync同步阻塞，要么addListener在监听处理中进行线程池关闭。
- netty的bug：发送消息前注意处理信道不通的情况；
- netty在java11的时候好像要进行额外配置？反正当天晚上临走前把jdk调成8了。

9.26 周二上午：
- 日志系统引入。在不使用springbootstarter的情况下引入日志系统，需要：
1. 接口依赖
2. 日志实现依赖
3. 配置文件，放在resources中。
这个之前有接触但是一直没有实操。导致实际使用的时候还是有点不顺。
开会之前完成了收发。开会晚之后上课，阿巴巴巴。

6.27 周三晚
早上歇着了，晚上进行一个课的翘
1. 动态代理实现
2. 尝试整理代码。包括异常处理、响应操作、包名调整、方法封装。
3. 实验室学长提出可以考虑引入spock进行单元测试，且可以由客户端指定获取结果时使用同步响应还是异步响应（类dubbo）。这个之后再议。以及客户端与服务端的tcp连接应当保持，而不是每次调用时进行连接。


6.28 周四：整理代码
整理代码完毕。首次无bug使用泛型：p   整理的内容包括以下方面：
- 包结构（主打一个随自己心意）
- RpcResponse内部封装调用结果数据，外部包含code和desc；
- （运行时和非运行时）异常统一由RpcException处理。
  - 部分异常通过assert+catch exception进行处理。
- 二三点的code统一使用枚举量进行处理；
- 封装代理实例方法，使其与被代理对象类型无关
- 提取静态utils；

上述的代码整理大概是在整理过程中自然而然地写出来了。也许是因为之前有看到过，最近开会的内容也是整理代码。

整理代码完成后得到的bug：
1. 传输实体内部的对象成员也需要实现可序列化接口
2. optional不可用于序列化。（https://cloud.tencent.com/developer/article/1767915）
3. Java 官方不推荐Optional用在<u>实体属性</u>上(当时有警告，但当时不理解为什么)。Optional 推荐的用法是在<u>函数返回值</u>上。告诉函数调用者，返回的对象存在空异常的可能，需要调用者自行处理。
    - "这也是 Java 设计出 NotSerializableException 异常的原因之一
    - 其次，Optional 作为一个包装类，大量的 Optional 会<u>消耗过多的内存</u>。Optional 在字段中使用可能会浪费内存，并减慢数据结构的遍历速度。 
    - 第三，官方也不推荐在序列化、永久存储或通过网络传输中使用 Optional。 
    - 第四，在<u>方法</u>的参数中，也不推荐使用 Optional。
    - 第五，官方推荐通过在 <u>Stream 流管道</u>（或其他方法）返回 Optional。
    - 最后，在序列化方面。JDK 的序列化比较特殊，需要同时向前及向后兼容。

周五周六，大概看了一下反射 异常处理和spi。不太清楚之后需要先做什么。

周天：
1. 添加了kryo序列化逻辑。添加功能的大概一般逻辑
   - 待添加框架或插件等的大概处理逻辑，本程序内部需要额外调用的有哪些方法；
   - 待添加的框架对象的初始化和消亡在本程序内部的位置
   - 本程序内部需要额外调用的方法与上一步设计的对象生命周期是否有冲突
   - 以及，对于已有代码添加额外功能，尽量减少原有代码的更改。根据原有代码的使用情况设计新功能类中提供的方法和入参和出参。
     - 比如此前没有参考guide的时候，自己是想，只把kryo的创建提取出类，而将使用kryo序列化和反序列化的所有逻辑嵌入原程序已有的decoder和encoderhandler中。
       - 重复代码过多，错误处理和kryo和文件资源释放也有点乱。对decoder和encoderhandler内部代码改动过多。
     - 而在guide中，提取出了serializer类接口，并将序列化与反序列化的方法全部封装到kryoSerilizer实现类中。根据decoder和encoder使用情况设计入参和出参。
     - 好处不言而喻，也利于此后其他序列化框架的引入。且encoder和decoder的改动也降到了最少。仍然可以获取到byte字节（利于获取长度）或反序列化得到的对象。

周一：
- 引入服务注册和发现逻辑，但是出现报错。Connection refused: /127.0.0.1:9848，并最终落到注册服务失败。
- 推测是nacos的问题。之前rms项目中也存在读取不到nacos配置的问题。两个方向：
  - 没有出现nacos的配置，这有点反常理；回顾nacos的启动，出现`docker run -d -p 8848:8848 -e MODE=standalone -v /Users/xubin/nacos/init.d/custom.properties:/home/nacos/init.d/custom.properties -v /Users/xubin/nacos/logs:/home/nacos/logs --restart always --name nacos nacos/nacos-server`
    - 即，涉及nacos/init.d/custom.properties文件，内部management.endpoints.web.exposure.include=*
    - 开放全部的对外监控的节点，这个应该没有问题
  - 另一个问题有可能是客户端与服务端无法创建链接；telnet试图连接nacos失败；
  - 在按照教程上在映射8848的端口之后额外映射9848，即添加-p 9848:9848即可正常连接。
- 此外，nacos只是提供了服务与地址的映射。实际服务获取的流程还是需要客户端和服务端来完成。也就是说，服务端在向nacos注册服务之外，对于自己提供的服务map还是需要在本地进行保存。本地保存的map与服务注册并不冲突。

- 在查看顶层的netty服务端和客户端启动过程中发现顶层启动流程还是有点乱。也许应当定义一个顶层的rpc服务端和客户端接口主类，netty来实现？类似于此前序列化器那样？todo 
- 包括异常处理handler和netty心跳与连接断开配置。在调试过程中出现异常会报"handler没有定义异常处理器，导致异常到达端点"的错。todo
- 对于nacos的扩展。1. 负载均衡；2. 除服务发布和订阅之外的服务器交互？当服务不可用时怎么办，这个是nacos做的、对客户端透明的还是需要自己实现的？

- 晚上：意思意思地实现了负载均衡。大概有用的和自己之前没想到的，一个是公有代码提取为虚类。这里需要注意的是，虚基类实现接口；而子类实现虚基类定义的内部策略代码；不要弄混。
- 另一个是轮询策略中对原子类和自旋锁的实操。此前自己都是纸上谈兵。

周二：
大致看了一下注解基本概念和如何使用。
   开始尝试实现"集成Spring通过注解注册服务和通过注解进行服务消费"
- 注解用于标记注册服务和消费服务。即对于服务端，将服务注册到nacos这一流程封装到注解内部逻辑；对于客户端，将从nacos获取服务封装到注解内部逻辑；
- 注解retention到哪？扩展的功能实现一般使用runtime吧？
    - 服务端的注解直接进行某包下的扫描然后统统注册到nacos里面。
    - 客户端的注解咋说，调用方法的时候接到代理那一块？
        - 试一下吧
        - 有哪些值？服务端可以提供自定义的注册到nacos中的与类名无关的服务名?但是默认值为空。这个似乎不太合适。guide那边是使用了group和version。自己没有想到那一块。暂时不包括吧。
        - 客户端也是一致的。
        - target：只能标注在类上？
        - 是否允许继承？应该允许。不过需要注意implements关系的话@Inherited是无效的。
    - 如何对注解进行处理？指定包名进行扫描。类似之前javaConfig的用法。注解用在main方法上。
    - 思考了一下，扫描和处理注解的逻辑不应当封装在注解内部。注解其实更类似一个bean。
    - 那既然有RpcScan了是不是不需要RpcScan了？姑且还是保留着吧。就像之前有了@Config了还得配置PackageScan。
        - 注解处理逻辑怎么说？第一反应是直接在ServerBoot的main函数中通过反射处理RpcScan和RpcService。客户端那边也同理。但是注解本身就应当对客户不可见。也就是说不应当让客户端和服务端的main函数主动来调用。这个怎么解决？todo。先实现扫描处理逻辑吧。
        - 非常、非常奇怪。建议还是先查看一下示例代码是怎么引入的。

在查看示例代码的过程中，跳到实现spi了。单纯看代码看不懂，还是需要落到外围用法中去。
- 注意之后读源码过程中还是得遵照"理解需求"->"初始理解代码（可能看不懂）"->"找其他人的相同机制实现或者查看外围用法"->自己尝试实现并进行对比来理解这样设计的原因   这样的思路来处理。

周三：
首先实现jdk版本的spi。实现结果如下。
```
KryoSerializer init
loadgithub.xunolan.rpcproject.serializer.kryo.KryoSerializer
init WhatEverSerializer
loadgithub.xunolan.rpcproject.serializer.kryo.WhatEverSerializer
[AppLog] 2023-10-04 18:30:09   客户端已启动
[AppLog] 2023-10-04 18:30:09   写入缓冲区
[AppLog] 2023-10-04 18:30:09   client send message
[AppLog] 2023-10-04 18:30:09   client收到响应
hhh0serverResponse
[AppLog] 2023-10-04 18:30:09   [e5d1c50d-157b-4ad2-a1e4-0a6d20314d56] Receive server push request, request = NotifySubscriberRequest, requestId = 13
[AppLog] 2023-10-04 18:30:09   [e5d1c50d-157b-4ad2-a1e4-0a6d20314d56] Ack server push request, request = NotifySubscriberRequest, requestId = 13
```
仅在客户端完成了spi的逻辑。可以看到两个类都加载了，且客户端使用的是kryo序列化。

其次开始逐步实现dubbo版本的spi。
从基本逻辑开始，对比自己的实现与guide代码实现，获取了以下知识点和trick：
- 在对两个static的map处理过程中有两点需要注意的：
  - 必须putIfAbsent而不能单纯put。因为当时没有加锁。有可能另一线程抢先初始化了。
    以及，在putIfAbsent之后也需要重新获取一次。否则稍后一些的线程返回的引用将与map中的引用不一致。这两部都是没有加锁导致的。
  - static Map不需要双重锁是因为，重复加载也没有关系？因为代价比较小，而且加载（初始化的）只是原始类。
    第一个static Map加载的是原始的ExtensionLoader对象；第二个是反射加载的Object；这些都是"只要其中一个可用就可以了，多加载一次也没关系"，也就是不需要保证"只加载一次"；
    （不知道这样理解对不对）
- 使用holder的目的是为了在value上包一层，使其具有volatile的特性，防止初始化对象时重排导致双重锁校验时返回无效的对象；
- 双重锁校验的static本质是为了操作同一个对象，即保证单例。因此在存在Map的情况下，内部Holder包裹的Extension实例可以保证操作同一个，就不必（也不能）static了。
- 双重锁校验的synchronized本质是为了使内部包裹的代码块只有一个线程能够进入。所以只要能够"锁住"，即不同线程操作的是同一个对象（spi机制中使用），或者类（双重锁校验的实例代码使用）都是可以的。

附：之前看双重锁的总结：
2. 单例模式双重锁
```java
public class penguin{
	private static volatile penguin m_peguin = null;
	private penguin(){}
	public static penguin getInstance(){
		if(m_penguin == null){
			synchronized(penguin.class){
				if(m_penguin==null){
					m_penguin = new penguin();
				}
			}
		}
		return m_penguin;
	}
}
```
- 详解：
    - 静态变量：保证单例；
    - volatile修饰；
    - 私有无参构造器：避免通过new初始化对象；
    - 静态的getInstance:
        - 第一个null判断：单例模式的逻辑，判断若空才进行首次初始化；
        - synchronized加类锁：多线程环境下，同时只能有一个线程进入同步代码块；
        - 同步代码块内部的二次验证（与volatile一起）：针对指令重排序场景的保证；如下：

对象初始化实际为三步。第二步有可能与第三步互换；此时：
- 先使实例指向地址，但未初始化对象；若此时另一线程进入到第二个null判断，发现非空，则会返回未初始化的对象；
    - 因此：volatile修饰以防止重排；
    - 第二次null验证防止在synchronized阻塞后获得锁时进行二次初始化；
        - 为什么synchronized不把第一个null也囊括？效率问题。不可能所有线程在getInstance时都要争取一下锁；
```
a. memory = allocate() //分配内存
b. ctorInstanc(memory) //初始化对象
c. instance = memory //设置instance指向刚分配的地址
```
