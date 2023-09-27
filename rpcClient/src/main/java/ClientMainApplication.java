import api.ServiceApi;
import netty.ClientInit;
import service.impl.ServiceInocationHandler;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class ClientMainApplication {
    public static void main(String[] args) {
        SocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",9999);
        ClientInit nettyClient = new ClientInit(inetSocketAddress);
        nettyClient.run();
        //传入要调用的对象？方法？
        ServiceApi service = (ServiceApi) Proxy.newProxyInstance(
                ServiceApi.class.getClassLoader(),
                new Class[]{ServiceApi.class},
                new ServiceInocationHandler(ServiceApi.class, nettyClient.getChannel())
        );
        String result = service.hello("hhh",0);
        System.out.println(result);

//      ServiceApi serviceApi = (ServiceApi) ServiceProxyFactory.getProxy(ServiceApi.class, nettyClient.getChannel());
//        String result = serviceApi.hello("hhh",0);


        //静态代理方法调用
//        ServiceApi service = new ServiceFactory().getService(nettyClient);
//        String result = service.hello("hhh",0);
//        System.out.println(result);
    }
}
