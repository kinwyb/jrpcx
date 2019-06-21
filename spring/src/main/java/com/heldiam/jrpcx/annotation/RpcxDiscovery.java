package com.heldiam.jrpcx.annotation;

import com.heldiam.jrpcx.core.discovery.IDiscovery;
import com.heldiam.jrpcx.core.discovery.SimpleDiscovery;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author kinwyb
 * @date 2019-06-21 10:53
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface RpcxDiscovery {
    /**
     * 初始化参数
     *
     * @return
     */
    String[] value() default {"127.0.0.1:8972"};

    /**
     * 默认路径
     * @return
     */
    String basePath() default "rpcx";

    /**
     * 实现类
     *
     * @return
     */
    Class<? extends IDiscovery> cls() default SimpleDiscovery.class;
}
