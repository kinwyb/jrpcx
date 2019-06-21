package com.heldiam.jrpcx.spring;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author kinwyb
 * @date 2019-06-21 08:54
 **/
public class MethodInterceptorImpl implements MethodInterceptor {

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("MethodInterceptorImpl:" + method.getName());
        return methodProxy.invokeSuper(o, objects);
    }
}
