package com.heldiam.jrpcx.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcxServer {
    /**
     * 服务器地址
     *
     * @return
     */
    String value() default "0.0.0.0:8972";
}
