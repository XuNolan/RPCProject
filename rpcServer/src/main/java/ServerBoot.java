import api.ServiceApi;
import netty.NettyServerInit;
import register.ServiceRegister;
import service.impl.ServiceImpl;

import java.net.InetSocketAddress;

public class ServerBoot {
    public static void main(String[] args) {
        //注册服务
        if(ServiceRegister.registerService(ServiceApi.class.getSimpleName(), ServiceImpl.class)){
            //启动rpc服务端；
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
            NettyServerInit RpcServer = new NettyServerInit(inetSocketAddress);
            RpcServer.run();
        }

    }
}
