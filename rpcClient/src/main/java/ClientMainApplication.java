import api.ServiceApi;
import netty.ClientInit;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ClientMainApplication {
    public static void main(String[] args) {
        SocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
        ClientInit nettyClient = new ClientInit(inetSocketAddress);
        nettyClient.run();
        ServiceApi service = new ServiceFactory().getService(nettyClient);
        String result = service.hello("hhh",0);
        System.out.println(result);
    }
}
