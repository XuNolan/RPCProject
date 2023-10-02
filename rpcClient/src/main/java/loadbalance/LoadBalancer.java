package loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

public interface LoadBalancer {
    InetSocketAddress getService(List<InetSocketAddress> services);
}
