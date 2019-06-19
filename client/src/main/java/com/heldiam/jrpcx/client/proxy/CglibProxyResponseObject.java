package com.heldiam.jrpcx.client.proxy;

import com.heldiam.jrpcx.client.Feature;
import com.heldiam.jrpcx.client.FeaturePool;
import com.heldiam.jrpcx.client.MetaData;
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

    private static final List<String> ignoreMethodName = Arrays.asList(
            "setRetObject",
            "setMetaData", "getMetaData",
            "getFeature", "setFeature",
            "getDone", "setDone");

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (ignoreMethodName.contains(method.getName())) {
            return proxy.invokeSuper(obj, args);
        }
        //代理的类
        String proxyClasssName = obj.getClass().getSuperclass().getSuperclass().getName();
        //方法执行类
        String proxyMethodClassName = method.getDeclaringClass().getName();
        if (proxyClasssName != proxyMethodClassName) { //如果不是代理类的方法,忽略直接执行
            return proxy.invokeSuper(obj, args);
        }
        isDone(obj); //等待对象设置结束了才能进行下一步操作
        return proxy.invokeSuper(obj, args);
    }

    public BeanMap getbean(Class cls) {
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
        beanMap.put("feature", f);
        beanMap.put("done", false);
        return beanMap;
    }

    /**
     * 获取feature
     *
     * @param beanMap
     * @return
     */
    Feature getFeature(BeanMap beanMap) {
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
                Object result = f.Done();
                FeaturePool.cloneObj(result, obj);
                if (obj instanceof MetaData) {
                    ((MetaData) obj).setMetaData(f.getMetaData());
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                beanMap.put("done", true);
                beanMap.put("feature", null);
                f.Close(); //释放feature
            }
        }
    }

}
