import netty.ServerInit;

import java.net.InetSocketAddress;

public class ServerMainApplication {
    public static void main(String[] args) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
        ServerInit RpcServer = new ServerInit(inetSocketAddress);
    }
}
