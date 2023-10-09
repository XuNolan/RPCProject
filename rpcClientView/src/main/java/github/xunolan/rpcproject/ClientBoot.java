package github.xunolan.rpcproject;


import cn.hutool.core.util.ObjectUtil;
import github.xunolan.rpcproject.annotation.Component;
import github.xunolan.rpcproject.annotation.PackageScan;
import github.xunolan.rpcproject.annotation.client.RpcClient;
import github.xunolan.rpcproject.annotation.client.RpcReference;
import github.xunolan.rpcproject.api.ServiceApi;
import github.xunolan.rpcproject.ioccontainer.ClientIocContainer;
import github.xunolan.rpcproject.ioccontainer.IocContainer;

@PackageScan(Packages = {"github.xunolan.rpcproject"})
@RpcClient(LoadBalance = "round")
@Component
public class ClientBoot {
    //理想状态下的调用关系：
    @RpcReference
    public ServiceApi service; //todo

    public static void main(String[] args) {
        IocContainer iocContainer = new ClientIocContainer(ClientBoot.class).initIocContainer().run();

        //使用
        ClientBoot clientBoot = (ClientBoot)iocContainer.getBeanFactory().getBean(ClientBoot.class.getName());
        String result = clientBoot.service.hello("hhh", 0);
        if (ObjectUtil.isNotNull(result))
            System.out.println(result);
        else
            System.out.println("客户端调用有误");


    }
}