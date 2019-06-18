package com.heldiam.jrpcx.client.proxy;

import com.heldiam.jrpcx.core.common.Feature;
import com.heldiam.jrpcx.core.common.FeaturePool;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 代理返回值对象
 *
 * @author heldiam
 */
class CglibProxyResponseObject implements MethodInterceptor {

    private static final List<String> ignoreMethodName = Arrays.asList("setRetObject", "getFeature", "setFeature", "getDone", "setDone");

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (ignoreMethodName.contains(method.getName())) {
            return proxy.invokeSuper(obj, args);
        }
        isDone(obj); //等待对象设置结束了才能进行下一步操作
        return proxy.invokeSuper(obj, args);
    }

    public <T> T getbean(Class<T> cls) {
        BeanGenerator generator = new BeanGenerator();
        generator.setSuperclass(cls);
        generator.addProperty("feature", Feature.class);
        generator.addProperty("done", boolean.class);
        Object o = generator.create();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(o.getClass());
        enhancer.setCallback(this);
        o = enhancer.create();
        BeanMap beanMap = BeanMap.create(o);
        Feature f = FeaturePool.get();
        f.setRetClass(cls);
        f.setRetObject(o);
        beanMap.put("feature", f);
        beanMap.put("done", false);
        return (T) beanMap.getBean();
    }

    /**
     * 获取feature
     *
     * @param obj
     * @return
     */
    Feature getFeature(Object obj) {
        BeanMap beanMap = BeanMap.create(obj);
        return (Feature) beanMap.get("feature");
    }

    /**
     * 是否已经结束
     *
     * @param obj
     * @return
     */
    void isDone(Object obj) throws Exception {
        BeanMap beanMap = BeanMap.create(obj);
        boolean ret = (boolean) beanMap.get("done");
        if (!ret) {
            Feature f = (Feature) beanMap.get("feature");
            try {
                f.Done();
            } catch (RuntimeException ex) {
                throw ex;
            } finally {
                beanMap.put("done", true);
                beanMap.put("feature", null);
                f.Close(); //释放feature
            }
        }
    }

}
