import api.ServiceApi;
import netty.NettyServerInit;
import register.LocalServiceRecord;
import registry.ServiceRegistry;
import registry.impl.NacosServiceRegistry;
import service.impl.ServiceImpl;

import java.net.InetSocketAddress;

public class ServerBoot {
    public static void main(String[] args) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
        //注册服务至本地
        if(LocalServiceRecord.registerService(ServiceApi.class.getSimpleName(), ServiceImpl.class)){
            //注册服务至nacos；
            ServiceRegistry serviceRegistry = new NacosServiceRegistry();
            serviceRegistry.register(ServiceApi.class.getSimpleName(), inetSocketAddress);
            //初始化客户端；
            NettyServerInit RpcServer = new NettyServerInit(inetSocketAddress);
            RpcServer.run();
        }
    }
}
