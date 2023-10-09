本文档用于记录实现的思路、实现选用的方法和方向等。用于确保当前的思路不会过于庞大和混乱。


1.如果需要实现一个基础的RPC框架，需要考虑什么：
- 最基础的RPC方法即为“远程过程调用”，客户端向服务端发送封装了调用信息和参数的报文，指定调用对应方法；服务端接收对应报文，提取有效信息，调用本地方法，并将结果封装为响应报文发送给客户端。
    - 需要注意的是，其基本原则是，“对于客户端来说，调用RPC方法就像调用本地方法一样”，也就是说，客户端只管调用具体的方法，而方法->报文封装、发送、接收响应报文并返回，都是对于客户端透明的。
- 其次，再考虑RPC==框架==的实现。也就是再引入注册调度中心。

当前：先实现RPC核心的方法，需要完成当client调用server方法时，数据流向server处理后返回结果。
- 划分三个模块，server和client。common模块定义server提供的方法，以及共有的接口和类模式。

1. 如何将客户端对接口的调用转换为Request请求并通过网络IO发送？
    - 不能让客户端构造（自行定义的）包装类完成此步操作。不必须确保"客户端对远程过程调用完全不可见"
    - 引入代理模式。静态代理需要预先得知客户端会调用的所有方法。不太符合要求。故使用动态代理。
        - 动态代理选型？存在接口。使用jdk动态代理实现即可。
        - 静态代理也行？

2. 如何完成应用层的同步调用的逻辑？
    - 也就是说，在客户端发起方法调用后，在未收到响应报文时，应当阻塞，直到收到报文。
    - Future方法。可以实现。

上述已经完成

接下来的目标是按着guide的优化路线继续，还是说先看spi？
按着guide的优化路线先写。稍微看了一下spi并不是特别能看懂，也许是放假放了一段时间脑子不转了；

这段时间还是以实操为主吧。
1. jdk改其他序列化；
- 是否有更好的序列化方式？
2. zookeeper管理相关地址信息。可否用nacos？https://juejin.cn/post/7068065361312088095
   - 选用nacos进行服务注册和发现。https://developer.aliyun.com/article/930139
   - nacos在之前已经配好了。启动docker即可。
此前，客户端在调用具体服务之前，需根据写死的inetSocket完成netty的初始化并连接到服务端。此前的项目结构只包含两个端点。
   - 引入nacos之后，引入第三个端点，即nacos服务监听和发现端点。
   - 服务端在netty的bind接收客户端请求之前，需首先将自己的地址和提供的服务提供至nacos处；
   - 客户端在与服务端建立连接之前，需首先与nacos端建立连接并获取服务的地址，根据得到的地址负载均衡再连接至对应的服务端。
   - 以及，nacos与服务端和客户端同时建立长连接；nacos监听到服务端服务发生变化时，主动以最小信息（事件+请求地址）通知客户端，客户端主动从对应的请求地址再次请求所需的信息。称为"推拉结合"
   - 从服务端的服务注册开始进行完善

3. 负载均衡
以下是看负载均衡的一些乱七八糟的笔记
负载均衡是什么：
- 路由到可处理请求的不同地址进行请求；
- 目前涉及到负载均衡的包括以下几个场景：
- 微服务调用。上述包括有客户端发现模式和服务端发现模式两种；
    - 对于客户端发现模式，客户端负责确定可用服务实例的网络位置和请求负载均衡；负载均衡与客户端强耦合。本RPC项目即此种方案；
    - 对于服务端发现模式，客户端（只需）向负载均衡器发出请求，==负载均衡器==查询服务注册中心并==将每个请求路由到可用的服务实例==。包括HTTP服务器和反向代理器（NGINX）可以作为服务端发现负载均衡器；四层和七层的负载均衡都属于服务端发现模式？即面向客户端透明。
- 目前两种场景使用场景：对于之前的项目，设备问注册调度要，注册调度查询redis；而可用的控制器服务在redis中注册自身。对于注册与设备的负载均衡，设备内部上写入注册和调度的url，使用四层（lvs）和七层（dns）对注册调度进行负载均衡；
    - 上述都是对设备透明的，都可以说属于服务端发现模式；
- 对于nacos集成的rpc，以本项目为例，属于客户端发现模式，客户端自己选择向哪个对端发起通信。
上述总结来说，对于负载均衡，没有固定的模式。从外部来看，重点涉及以下几点：
- 负载均衡对象（或者节点）从哪获取备选对象列表（一般是服务地址）；
    - 对于nacos，服务端主动注册；对于sdn项目，自己从redis中拉取心跳；
- 负载均衡对象（或者节点）是单节点还是某个模块的一部分，为客户端提供所有的备选对象还是返回一种（即对客户端透明还是强耦合）
    - 目前业界主流的负载均衡方案可分成两类：
        - 第一类：==集中式负载均衡==， 即在 consumer 和 provider 之间使用独立的负载均衡设施(可以是硬件，如 F5, 也可以是软件，如 nginx), 由该设施负责把 访问请求 通过某种策略转发至 provider；
        - 第二类：==进程内负载均衡==，将负载均衡逻辑集成到 consumer，consumer 从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选择出一个合适的 provider。Ribbon 就属于后者，它只是一个类库，集成于 consumer 进程，consumer 通过它来获取到 provider 的地址

- 正向代理面向服务器隐藏了真正的客户端；反向代理面向客户端隐藏了真正的服务器。

nginx：”若让client去选择具体的server，若其中一个server挂了，client是无法提前感知到的“
- nginx就能提前感知到了？
- 在流量打到server前再做一层鉴权操作。通过鉴权后才允许流量打到server上--网关。
- Nginx 是七层（即应用层）负载均衡器 ，这意味着如果它要转发流量首先得和 client 建立一个 TCP 连接，并且转发的时候也要与转发到的上游 server 建立一个 TCP 连接，而我们知道建立 TCP 连接其实是需要耗费内存（TCP Socket，接收/发送缓存区等需要占用内存）的，客户端和上游服务器要发送数据都需要先发送暂存到到 Nginx 再经由另一端的 TCP 连接传给对方。
- Nginx 的负载能力较差主要是因为它是七层负载均衡器必须要在上下游分别建立两个 TCP 所致，那么是否能设计一个类似路由器那样的只负载转发包但不需要建立连接的负载均衡器呢，这样由于不需要建立连接，只负责转发包，不需要维护额外的 TCP 连接，它的负载能力必然大大提升，于是四层负载均衡器 LVS 就诞生了，
    - 也就是说，lvs即修改报头目的地址、（根据需要）修改源地址。
    - 负载均衡设备在接收到第一个来自客户端的SYN 请求时，即通过负载均衡算法选择一个最佳的服务器，并对报文中目标IP地址进行修改(改为后端服务器 IP ），直接转发给该服务器。TCP 的连接建立，即三次握手是客户端和服务器直接建立的，负载均衡设备只是起到一个类似路由器的转发动作。在某些部署情况下，为保证服务器回包可以正确返回给负载均衡设备，在转发报文的同时可能还会对报文原来的源地址进行修改。
- 此外，nginx在1.9之后也开始支持四层负载均衡了。
    - 通过部署多台 Nginx 的方式在流量不是那么大的时候确实是可行，但 LVS 是 Linux 的内核模块，工作在内核态，而 Nginx 工作在用户态，也相对比较重，所以在性能和稳定性上 Nginx 是不如 LVS 的，这就是为什么我们要采用 LVS + Nginx 的部署方式。

ribbon： ribbon相当于嵌入消费者代码内部的工具。”只是一个类库，集成于consumer进程，consumer通过它获取到provider的地址。“
如果不想要因为ribbon来引入spring cloud的话，自己手写负载均衡器，是不是不需要引入ribbon？
是的，不需要ribbon。
一致性哈希（以下来自小林coding）
- "轮询这类的策略只能适用与每个节点的数据都是相同的场景，访问任意节点都能请求到数据。但是不适用分布式系统，因为分布式系统意味着数据水平切分到了不同的节点上，访问数据的时候，一定要寻址存储该数据的节点。"
- 一致性哈希是指将「存储节点」和「数据」都映射到一个首尾相连的哈希环上，如果增加或者移除一个节点，仅影响该节点在哈希环上顺时针相邻的后继节点，其它数据也不会受到影响。


- [X]  **使用开源的序列化机制 Kyro（也可以用其它的）替代 JDK 自带的序列化机制；**
- [X]  **使用 Zookeeper 管理相关服务地址信息**
- [ ]  Netty 重用 Channel 避免重复连接服务端
- [X]  使用 `CompletableFuture` 包装接受客户端返回结果（之前的实现是通过 `AttributeMap` 绑定到 Channel 上实现的） 详见：使用 CompletableFuture 优化接受服务提供端返回结果
- [ ]  **增加 Netty 心跳机制** : 保证客户端和服务端的连接不被断掉，避免重连。
- [x]  **客户端调用远程服务的时候进行负载均衡** ：调用服务的时候，从很多服务地址中根据相应的负载均衡算法选取一个服务地址。ps：目前仅实现了随机负载均衡算法
- [ ]  **处理一个接口有多个类实现的情况** ：对服务分组，发布服务的时候增加一个 group 参数即可。
- [ ]  **集成 Spring 通过注解注册服务**
- [ ]  **集成 Spring 通过注解进行服务消费** 。
- [ ]  **增加服务版本** ：建议使用两位数字版本，如：1.0，通常在接口不兼容时版本号才需要升级。为什么要增加服务版本号？为后续不兼容升级提供可能，比如服务接口增加方法，或服务模型增加字段，可向后兼容，删除方法或删除字段，将不兼容，枚举类型新增字段也不兼容，需通过变更版本号升级。
- [ ]  **对 SPI 机制的运用**
- [ ]  **增加可配置比如序列化方式、注册中心的实现方式,避免硬编码** ：通过 API 配置，后续集成 Spring 的话建议使用配置文件的方式进行配置
- [ ]  **客户端与服务端通信协议（数据包结构）重新设计** ，可以将原有的 `RpcRequest`和 `RpcReuqest` 对象作为消息体，然后增加如下字段（可以参考：《Netty 入门实战小册》和 Dubbo 框架对这块的设计）：
    - **魔数** ： 通常是 4 个字节。这个魔数主要是为了筛选来到服务端的数据包，有了这个魔数之后，服务端首先取出前面四个字节进行比对，能够在第一时间识别出这个数据包并非是遵循自定义协议的，也就是无效数据包，为了安全考虑可以直接关闭连接以节省资源。
    - **序列化器编号** ：标识序列化的方式，比如是使用 Java 自带的序列化，还是 json，kyro 等序列化方式。
    - **消息体长度** ： 运行时计算出来。
    - ......
- [ ]  **编写测试为重构代码提供信心**
- [ ]  **服务监控中心（类似dubbo admin）**
- [ ]  **设置 gzip 压缩**

全部回滚。注解思路移入dailyRecord；
开始尝试实现SPI；
SPI不是对服务提供者提供的服务进行选择，而是对所有客户代码（注意不是指客户端代码，而是指客户(使用SPI机制的程序员)需要实现的代码，包括客户端和服务端）使用到的的不同服务进行的处理；
面向接口编程。spi注解在接口上。
例如序列化选型，例如服务注册和发现的zookeeper或者nacos选型。

简单版本的spi完成。参照了javaguide。因为双重锁校验之前并不是特别熟练，也没有实操过。
一些思考和思路全部写在这段时间commit代码的内部注释中了。之后回顾双重锁校验可以回顾这一块。


再次回滚。最初是想实现使用注解进行服务端的服务注册与客户端的调用。达到以下效果：

之前模模糊糊是感觉 对Spring中的Bean生命周期以及各钩子函数不是特别了解。学长建议是直接自己实现一个IOC。
重新看了一遍Bean注册和依赖注入过程，以及完全理解了循环依赖，重新试图自己实现一个IOC。

ioc重要的几点包括：
- IOC如何对不同的Bean配置进行解析和加载；
  - 这里包括需要获取和存放哪些Bean的参数，即BeanDefinition包括哪些属性。另一方面包括如何将配置文件转换为BeanDefinition
    - BeanDefinition和BeanDefinitionRegistry和BeanDefinitionReader；
    - 以及包括ApplicationContext的接口实现类，定义了将不同Bean的配置方式进行不同的资源加载。
- IOC如何根据BeanDefinition定义生成Bean实例，并放置在容器内部；
  - 基础的包括Bean如何根据BeanDefinition生成实例，还有包括Bean依赖注入。还有Bean嵌套和缓存。
    - BeanFactory的部分？或者说是ApplicationContext？
- 外界如何获取容器中的Bean。
  - 注解、或者其他自动扫描注入等，根据名字或类的类型等。
    - BeanFactory

对于第一点，使用注解方式进行。依赖注入的使用Autowired、注册的使用Component。Autowired默认使用byType。结合Qualifier则为byName;
先实现Bean注册。

吃了个饭 路上思考了一下。其实IOC的本质就是扫描注解->提取信息转换为beanDefinition，放到BeanFactory->BeanFactory在容器初始化过程中对Bean进行实例化->引入Bean的生命周期、依赖注入等进行处理；
因此也就是说，并不需要按照spring的那样实现ioc。完全可以针对RPCService等等一系列注解去实现对rpc本项目特化的。重点只是说能使本框架去控制对象的生成，而对使用框架的客户不可见。
从服务端开始。
服务端需要对框架不可见的服务提供者，提供服务注册功能和客户端TCP连接功能。RpcServer提供扫描的服务包、host的ip和port。
内部提供的服务为RpcService。

```java

import github.xunolan.rpcproject.api.ServiceApi;
import cn.hutool.core.util.ObjectUtil;
import github.xunolan.rpcproject.extension.ExtensionLoader;
import github.xunolan.rpcproject.loadbalance.LoadBalancer;
import github.xunolan.rpcproject.loadbalance.impl.RandomLoadBalance;
import github.xunolan.rpcproject.netty.NettyClientInit;
import github.xunolan.rpcproject.proxy.ProxyFactory;
import github.xunolan.rpcproject.registry.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.List;

public class ClientBoot {
    public static void main(String[] args) {
        ServiceRegistry registry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("nacos");
        //服务发现 + 负载均衡
        List<InetSocketAddress> addresses = registry.lookUpService(ServiceApi.class.getSimpleName());
        LoadBalancer loadBalancer = new RandomLoadBalance();
        InetSocketAddress targetAddress = loadBalancer.getService(addresses);//这个负载均衡感觉也就是意思意思（）
        //建立远端连接
        NettyClientInit nettyClient = new NettyClientInit(targetAddress);
        nettyClient.run();
        //根据泛型获取代理
        ServiceApi service = new ProxyFactory<ServiceApi>().getProxy(ServiceApi.class, nettyClient);
        String result = service.hello("hhh",0);
        if(ObjectUtil.isNotNull(result))
            System.out.println(result);
        else
            System.out.println("客户端调用有误");
    }
}

```
客户端相比服务端存在的一个额外问题是，如何对成员进行依赖注入。
我能够生成bean，如何通过注解进行返回？在注解处理函数中保留引用？不对。这里应该涉及到的知识点是"依赖注入"。一种讨巧的方法是通过setter进行依赖注入。这样的话就不需要针对再外一层的Bean进行维护。
如果想要通过注解方式（field方式）实现依赖注入呢？这样也不是构造器方式的依赖注入？
所以说Spring是怎么处理的？内部需要依赖注入的对象，其本身也不一定是Bean啊？
- 不行。Spring的话，必须是交给Spring的IOC容器来管理的类，才能进行依赖注入。否则需要用户自行实现相关的反射代码自行注入。

- 因此，估计还是需要在对rpc服务进行注解的外部类上引入类似Component的注解，指定其由ioc容器来管理。
- 思考一下是不是需要二层缓存：思考结果是需要的。绕不过去。
- 梳理一下整个思考的思路：本意是想只将注解了@RpcReference的对象替换为代理。但是存在一个问题：在什么时机进行这样的替换动作？也就是，什么时候将这个代理对象注入至注解了@RpcReference的对象的父类内部。这样说是不是比较熟悉："替换"就是Spring的依赖注入过程。
  - 而spring要求的，必须是"交给Spring的IOC容器来管理的类，才能进行依赖注入"。因为spring无法得知jdk的new的时机。spring只能管理自己能够管理的对象。
  - 也就是说，注解了@RpcReference的对象所属的对象，也必须是由spring管理的对象。其类，需要引入Component注解。
  - 然后就，干脆就也引入Autowired了。二层缓存也是需要的（二层缓存基本上也和依赖注入绑定了。）
  - 以及，此前内部注释有提到过，这里再记录一下：对于扫描Bean配置过程中，为什么要先转换为BeanDefinition，再统一实例化?主要是由于存在依赖注入，需要以统一的视图管理所有的对象关系和引用关系。所以不能直接边扫描边实例化对象，只能保存一个中间数据，然后再统一进行实例化。


开始统一重新设计ioc模块。进行大改动。
1. 总体结构重新设计：

2. 重新考虑构造方法相关。涉及的改动包括
   - Component注解的类的规范：定义其必须包括无参构造器、属性的依赖注入方式选择为setter方式（因为构造函数方式注入无法解决循环依赖问题，以及为了实现方便，set的话就只需要考虑对象的依赖注入问题。）
   - Autowired必须注解在set方法上。
   - BeanDefinition内部需要包含对应的构造函数。

暂存，反思了一下，原注解在成员变量上的注入方式是可行的，之前还是对依赖注入理解不深。以及BeanDefinition可以简化，不需要保留原有的autowired和RpcReference两个map。在创建Bean的时候再进行内部依赖的扫描和Bean的创建。这样对于递归实例化Bean的流程更加清晰。
全部重开。
