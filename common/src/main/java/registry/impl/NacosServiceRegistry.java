package registry.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import enums.ExceptionEnum;
import exception.RpcException;
import registry.ServiceRegistry;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class NacosServiceRegistry implements ServiceRegistry {
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;
    static {
        try{
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        }catch (NacosException e){
            throw new RpcException(ExceptionEnum.ConnectToServiceRegistryFail, e);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e){
            throw new RpcException(ExceptionEnum.RegisterToServiceFail, e);
        }
    }

    @Override
    public List<InetSocketAddress> lookUpService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            List<InetSocketAddress> address = new ArrayList<>();
            instances.forEach(i -> address.add(new InetSocketAddress(i.getIp(), i.getPort())));
            return address;
        } catch (NacosException e) {
            throw new RpcException(ExceptionEnum.GetServiceFail, e);
        }
    }
}
