import netty.ServerInit;
import service.ServiceRegister;
import service.impl.ServiceImpl;

import java.net.InetSocketAddress;

public class ServerMainApplication {
    public static void main(String[] args) {
        ServiceRegister.registerService("ServiceApi", ServiceImpl.class);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
        ServerInit RpcServer = new ServerInit(inetSocketAddress);
        RpcServer.run();

    }
}
