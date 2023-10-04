package github.xunolan.rpcproject.loadbalance.impl;

import github.xunolan.rpcproject.loadbalance.LoadBalancer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//轮询 基本思想即为内部维护一个index变量，
//当service大小和此前不一样怎么办？若下一个大于当前大小，则归零？
public class RoundLoadBalance extends AbstractLoadBalance implements LoadBalancer {
    //需要考虑并发问题。示例使用的是自旋锁？
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    public final int getAndIncrement(){
        int current;
        int next;
        do {
            current = this.atomicInteger.get();
            // 不能超过Integer的最大值
            next = current == Integer.MAX_VALUE ? 0 : current + 1;
        } while (! this.atomicInteger.compareAndSet(current, next));
        return next;
    }

    @Override
    public InetSocketAddress selectInstance(List<InetSocketAddress> services) {
        int index = getAndIncrement()%services.size();
        return services.get(index);
    }
}
