package com.heldiam.jrpcx.client.proxy;


import com.heldiam.jrpcx.client.Client;
import com.heldiam.jrpcx.client.Feature;
import net.sf.cglib.beans.BeanMap;
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
        String serviceName = method.getDeclaringClass().getName();
        serviceName = serviceName.substring(serviceName.lastIndexOf(".")).replaceAll("\\.", "");
        Class cls = method.getReturnType();
        BeanMap ret = cgResp.getbean(cls);
        Feature feature = cgResp.getFeature(ret);
        Object params = null;
        if (args.length > 0) {
            params = args[0];
        }
        feature.Call(client, serviceName, methodName, params);
        return ret.getBean();
    }

}
