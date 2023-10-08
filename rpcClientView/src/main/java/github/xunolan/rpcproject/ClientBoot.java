package github.xunolan.rpcproject;


import cn.hutool.core.util.ObjectUtil;
import com.sun.security.ntlm.Client;
import github.xunolan.rpcproject.annotation.client.RpcClient;
import github.xunolan.rpcproject.annotation.client.RpcReference;
import github.xunolan.rpcproject.api.ServiceApi;

@RpcClient(ServicePacketScan = {"github.xunolan.rpcproject"})
public class ClientBoot {
    //理想状态下的调用关系：
    @RpcReference
    private ServiceApi service;

    public static void main(String[] args) {
        IocContainer iocContainer = new IocContainer(ClientBoot.class).initIocContainer().run();

        //使用
        ClientBoot clientBoot = new ClientBoot();
        String result = clientBoot.service.hello("hhh", 0);
        if (ObjectUtil.isNotNull(result))
            System.out.println(result);
        else
            System.out.println("客户端调用有误");


    }
    private void test(){
        RpcReference rpcReference = this.service.getClass().getAnnotation(RpcReference.class);
    }
    //涉及bean的生命周期处理过程。需要在Bean依赖注入，要么就是初始化过程中将传统的对service这个Bean的初始化变成代理连接等一系列操作。
    //或者分成两部分。新增RpcClient注解，在此注解中处理与服务端的连接？
    //不好。还是统一在 RpcReferce。其他的尽量对客户端不可见。
    //或者说，在RpcClient内部负责定义不同的负载均衡策略等？以及也像之前那样RpcClient定义扫描的包，只处理包内部注解了RpcReference的成员？
    //回来。这里的本质相当于在service外面的bean初始化过程中又包了一层代理，或者说将获取代理的步骤封装到注解处理函数内部了。

}