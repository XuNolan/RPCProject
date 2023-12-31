package github.xunolan.rpcproject.annotation.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcClient {
    String LoadBalance() default "random";
    String ServiceDiscovery() default "nacos";
}
