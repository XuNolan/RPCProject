import github.xunolan.rpcproject.IocContainer;
import github.xunolan.rpcproject.annotation.server.RpcServer;

@RpcServer(ServicePacketScan = {"service"})
public class ServerBoot {


    public static void main(String[] args) {
        IocContainer iocContainer = new IocContainer(ServerBoot.class).initIocContainer().run();
    }
}
