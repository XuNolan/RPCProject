package github.xunolan.rpcproject;

import github.xunolan.rpcproject.annotation.Component;
import github.xunolan.rpcproject.annotation.PackageScan;
import github.xunolan.rpcproject.annotation.server.RpcServer;
import github.xunolan.rpcproject.ioccontainer.IocContainer;
import github.xunolan.rpcproject.ioccontainer.ServerIocContainer;

@PackageScan(Packages = {"github.xunolan.rpcproject"})
@RpcServer
@Component
public class ServerBoot {
    public static void main(String[] args) {
        IocContainer iocContainer = new ServerIocContainer(ServerBoot.class).initIocContainer();
    }
}
