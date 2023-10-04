package github.xunolan.rpcproject.loadbalance.impl;

import github.xunolan.rpcproject.loadbalance.LoadBalancer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

// 随机
public class RandomLoadBalance extends AbstractLoadBalance implements LoadBalancer  {

    @Override
    public InetSocketAddress selectInstance(List<InetSocketAddress> services) {
        Random random = new Random(1234);
        return services.get(random.nextInt(services.size()));
    }
}
