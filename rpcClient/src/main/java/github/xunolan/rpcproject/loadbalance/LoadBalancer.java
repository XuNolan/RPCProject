package github.xunolan.rpcproject.loadbalance;

import github.xunolan.rpcproject.extension.SPI;

import java.net.InetSocketAddress;
import java.util.List;

@SPI
public interface LoadBalancer {
    InetSocketAddress getService(List<InetSocketAddress> services);
}
