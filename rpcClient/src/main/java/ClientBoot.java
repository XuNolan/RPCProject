import api.ServiceApi;
import cn.hutool.core.util.ObjectUtil;
import netty.NettyClientInit;
import proxy.ProxyFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ClientBoot {
    public static void main(String[] args) {
        //启动rpc客户端；
        SocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
        NettyClientInit nettyClient = new NettyClientInit(inetSocketAddress);
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
