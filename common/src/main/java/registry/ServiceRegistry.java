package registry;

import java.net.InetSocketAddress;
import java.util.List;

public interface ServiceRegistry {
    // 服务名称和地址注册进服务注册中心；
    void register(String serviceName, InetSocketAddress inetSocketAddress);
    // 根据服务名称从注册中心获取到服务提供者的地址；(示例为单个地址，这里自己认为应当为List，因为之后还需要负载均衡)
    List<InetSocketAddress> lookUpService(String serviceName);
}
