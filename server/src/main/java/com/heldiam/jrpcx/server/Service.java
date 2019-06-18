package com.heldiam.jrpcx.server;

import com.heldiam.jrpcx.core.codec.ICodec;
import com.heldiam.jrpcx.core.common.RpcException;
import com.heldiam.jrpcx.core.protocol.Message;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kinwyb
 * @date 2019-06-15 08:15
 **/
public class Service {

    private static final Map<String, ServiceProxy> serviceMap = new HashMap<>();

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Service.class.getName());

    /**
     * 获取服务
     *
     * @param serviceName 服务名称
     * @return
     */
    public static Object getService(String serviceName) {
        return serviceMap.get(serviceName);
    }

    /**
     * 注册服务
     *
     * @param obj
     */
    public static void registerService(Object obj, Class interfaceClass) {
        ServiceProxy proxy = new ServiceProxy(obj, interfaceClass);
        if (proxy.isNotService()) {
            LOG.error(proxy.getServiceName() + "不是有效服务对象");
            return;
        }
        serviceMap.put(proxy.getServiceName(), proxy);
    }

    /**
     * 获取所有服务
     *
     * @return
     */
    public static String[] getAllService() {
        return serviceMap.keySet().toArray(new String[]{});
    }

    /**
     * 服务反射调用
     *
     * @param msg
     * @return
     * @throws Exception
     */
    public static Object ServiceInvoke(Message msg, ICodec codec) throws Exception {
        ServiceProxy proxy = serviceMap.get(msg.servicePath);
        if (proxy == null) {
            throw new RpcException("服务不存在");
        }
        return proxy.invoke(msg.serviceMethod, msg.payload, codec);
    }

}
