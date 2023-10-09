package github.xunolan.rpcproject.annotation.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcReference {
    Class<?> ImpService();
    String Gruop() default "";
    String Version() default "";
}
