import api.ServiceApi;
import cn.hutool.core.util.ObjectUtil;
import loadbalance.LoadBalancer;
import loadbalance.impl.RandomLoadBalance;
import netty.NettyClientInit;
import proxy.ProxyFactory;
import registry.ServiceRegistry;
import registry.impl.NacosServiceRegistry;

import java.net.InetSocketAddress;
import java.util.List;

public class ClientBoot {
    public static void main(String[] args) {
        ServiceRegistry registry = new NacosServiceRegistry();
        List<InetSocketAddress> addresses = registry.lookUpService(ServiceApi.class.getSimpleName());
        LoadBalancer loadBalancer = new RandomLoadBalance();
        InetSocketAddress targetAddress = loadBalancer.getService(addresses);//这个负载均衡感觉也就是意思意思（）
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
