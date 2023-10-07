package github.xunolan.rpcproject.annotation.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcService {
    Class<?> ImplementClazz();
    String Version() default "";
    String Group() default "";
}
