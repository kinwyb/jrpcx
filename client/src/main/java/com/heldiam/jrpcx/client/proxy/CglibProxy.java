package com.heldiam.jrpcx.client.proxy;


import com.heldiam.jrpcx.client.Client;
import com.heldiam.jrpcx.client.Feature;
import com.heldiam.jrpcx.client.FeaturePool;
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
        Object params = null;
        if (args.length > 0) {
            params = args[0];
        }
        String methodName = method.getName();
        String serviceName = method.getDeclaringClass().getName();
        serviceName = serviceName.substring(serviceName.lastIndexOf(".")).replaceAll("\\.", "");
        Class cls = method.getReturnType();
        if (cls.getName() == "void") { //没有返回值
            Feature f = FeaturePool.get();
            f.Call(client, serviceName, methodName, params);
            return null;
        }
        try { //生成异步代理请求
            BeanMap ret = cgResp.getbean(cls);
            Feature feature = cgResp.getFeature(ret);
            feature.Call(client, serviceName, methodName, params);
            return ret.getBean();
        } catch (Exception ex) { //如果无法通过异步方式请求,则直接用同步的方式请求
            Feature f = FeaturePool.get();
            f.setRetClass(cls);
            f.Call(client, serviceName, methodName, params);
            return f.getRetObject();
        }
    }

}
