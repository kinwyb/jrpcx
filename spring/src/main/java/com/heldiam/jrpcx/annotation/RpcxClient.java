package com.heldiam.jrpcx.annotation;

import com.heldiam.jrpcx.client.failMode.FailModeEnum;
import com.heldiam.jrpcx.client.selector.SelectorMode;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcxClient {

    /**
     * 选择模式
     *
     * @return
     */
    SelectorMode selectorMode() default SelectorMode.RoundRobin;

    /**
     * 失败模式
     *
     * @return
     */
    FailModeEnum failMode() default FailModeEnum.Failtry;

}
