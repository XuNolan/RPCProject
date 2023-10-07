package github.xunolan.rpcproject.annotation.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcServer {
    String[] ServicePacketScan();
    String Host() default "127.0.0.1";
    int Port() default 9999;
}
