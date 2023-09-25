import api.ServiceApi;
import service.impl.ServiceImpl;

public class ServiceFactory {
    public ServiceApi getService(){
        ServiceApi serviceApi = new ServiceImpl();
        return serviceApi;
    }
}
