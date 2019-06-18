package com.heldiam.jrpcx.client.proxy;


import com.heldiam.jrpcx.client.Client;
import com.heldiam.jrpcx.core.common.Feature;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 代理类
 *
 * @author heldiam
 */
class CglibProxy implements MethodInterceptor {

    final CglibProxyResponseObject cgResp = new CglibProxyResponseObject();
    final Client client;

    public CglibProxy(Client client) {
        this.client = client;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();
        String className = method.getDeclaringClass().getName();
        className = className.substring(className.lastIndexOf(".")).replaceAll("\\.", "");
        Class cls = method.getReturnType();
        Object ret = cgResp.getbean(cls);
        Feature feature = cgResp.getFeature(ret);
        try {
            if (args.length > 0) {
                client.Call(className, methodName, args[0], feature);
            } else {
                client.Call(className, methodName, null, feature);
            }
        } catch (Exception ex) {
            feature.setException(ex);
            feature.setResult(null);
        }
        return ret;
    }

}
