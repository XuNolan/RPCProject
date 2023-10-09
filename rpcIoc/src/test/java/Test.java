import github.xunolan.rpcproject.annotation.Autowired;
import github.xunolan.rpcproject.annotation.Component;
import github.xunolan.rpcproject.annotation.client.RpcReference;
import github.xunolan.rpcproject.api.ServiceApi;

import java.util.HashMap;
import java.util.Map;

public class Test {
    @Component
    class ServiceA{
        String a;
        @Autowired
        ServiceB b;
        @RpcReference//Rpc服务注入
        ServiceApi rpcService;
        public void setA(String a){
            this.a = a;
        }
        //b和rpcService可将直接使用。
        //定义不会对未注解Autowired的成员和基本类型成员进行依赖注入。

    }
    @Component
    class ServiceB{
        Map<String, String> BMap = new HashMap<>();
    }
}
