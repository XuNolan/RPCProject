package loadbalance.impl;

import loadbalance.LoadBalancer;

import java.net.InetSocketAddress;
import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalancer {
    public InetSocketAddress getService(List<InetSocketAddress> services){
        if (services == null || services.size()==0){
            return null;
        }if (services.size() == 1) {
            return services.get(0);
        }
        return selectInstance(services);
    }

    protected abstract InetSocketAddress selectInstance(List<InetSocketAddress> services);
}
