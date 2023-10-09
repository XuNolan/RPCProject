import github.xunolan.rpcproject.annotation.Autowired;
import github.xunolan.rpcproject.annotation.Component;
import github.xunolan.rpcproject.annotation.client.RpcReference;
import github.xunolan.rpcproject.api.ServiceApi;

public class Test {
    @Component
    class ServiceA{
        String a;
        ServiceB b;
        ServiceApi rpcService;
        public void setA(String a){
            this.a = a;
        }
        @Autowired
        public void setB(ServiceB b){ //普通对象注入
            this.b = b;
        }
        @RpcReference(ImpService = ServiceApi.class)//Rpc服务注入
        public void setRpcService(ServiceApi rpcService){
            this.rpcService = rpcService;
        }

    }
    @Component
    class ServiceB{
        String aInb;
        public void setaInb(String aInb){
            this.aInb = aInb;
        }
    }
}
