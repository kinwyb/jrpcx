package com.heldiam.jrpcx.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * rpcx服务注解
 *
 * @author kinwyb
 * @date 2019-06-20 14:50
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface RpcxService {
    Class Interface(); //服务接口
}
