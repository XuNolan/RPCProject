import api.ServiceApi;
import netty.ClientInit;
import service.impl.ServiceImpl;

public class ServiceFactory {
    public ServiceApi getService(ClientInit clientInit){
        ServiceApi serviceApi = new ServiceImpl(clientInit);
        return serviceApi;
    }
}
