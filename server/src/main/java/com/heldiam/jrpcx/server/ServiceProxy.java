package com.heldiam.jrpcx.server;

import com.heldiam.jrpcx.core.codec.ICodec;
import com.heldiam.jrpcx.core.common.RpcException;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kinwyb
 * @date 2019-06-15 08:24
 **/
class ServiceProxy {

    private final Class targetClass;
    private final Object targetObject;
    private final Map<String, ServiceMethod> methodMap = new HashMap<>();
    private final String serviceName;
    private boolean notService = true;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Service.class.getName());

    public ServiceProxy(Object targetObject, Class interfaceClass) {
        this.targetObject = targetObject;
        this.targetClass = interfaceClass;
        this.serviceName = interfaceClass.getSimpleName(); //服务名称
        this.parseService();
    }

    /**
     * 解析服务
     */
    private void parseService() {
        Method[] methods = targetClass.getMethods();
        for (Method m : methods) {
            String methodName = m.getName();
            Class returnClass = m.getReturnType();
            Class[] argClass = m.getParameterTypes();
            if (argClass.length != 1) {
                continue;
            }
            methodMap.put(methodName, new ServiceMethod(argClass[0], returnClass, m));
            LOG.debug("注册服务[" + serviceName + "." + methodName + "]");
        }
        this.notService = methodMap.keySet().size() < 1;
    }

    /**
     * 是否有效服务对象
     *
     * @return
     */
    public boolean isNotService() {
        return notService;
    }

    /**
     * 服务名称
     *
     * @return
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * 反射调用
     *
     * @param serviceMethod
     * @param argData
     * @param codec
     * @return
     * @throws Exception
     */
    public Object invoke(String serviceMethod, byte[] argData, ICodec codec) throws Exception {
        ServiceMethod method = methodMap.get(serviceMethod);
        if (method == null) {
            throw new RpcException(getServiceName() + "服务方法[" + serviceMethod + "]不存在");
        }
        if (method.argClass != null) {
            Object arg = codec.decode(argData, method.argClass);
            return method.method.invoke(targetObject, arg);
        } else {
            return method.method.invoke(targetObject);
        }
    }

    class ServiceMethod {
        final Class argClass;
        final Class returnClass;
        final Method method;

        ServiceMethod(Class argClass, Class returnClass, Method method) {
            this.argClass = argClass;
            this.returnClass = returnClass;
            this.method = method;
        }
    }

}
