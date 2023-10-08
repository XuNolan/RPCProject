import github.xunolan.rpcproject.IocContainer;
import github.xunolan.rpcproject.annotation.PackageScan;
import github.xunolan.rpcproject.annotation.server.RpcServer;

@PackageScan(Packages = {"github.xunolan.rpcproject"})
public class ServerBoot {

    public static void main(String[] args) {
        IocContainer iocContainer = new IocContainer(ServerBoot.class).initIocContainer().run();
    }
}
