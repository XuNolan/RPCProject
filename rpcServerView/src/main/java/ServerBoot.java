import github.xunolan.rpcproject.ioccontainer.IocContainer;
import github.xunolan.rpcproject.annotation.PackageScan;

@PackageScan(Packages = {"github.xunolan.rpcproject"})
public class ServerBoot {

    public static void main(String[] args) {
        IocContainer iocContainer = new IocContainer(ServerBoot.class).initIocContainer().run();
    }
}
