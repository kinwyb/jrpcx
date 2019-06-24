package com.heldiam.jrpcx.core.discovery;

import com.heldiam.jrpcx.core.common.RpcException;
import com.heldiam.jrpcx.core.common.StringUtils;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author kinwyb
 * @date 2019-06-17 12:57
 **/
abstract class BaseDiscovery implements IDiscovery, IDiscoveryWatch {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BaseDiscovery.class.getName());

    private String BasePath = "rpcx";

    private List<IDiscoveryWatch> watchList = new LinkedList<>();

    protected Map<String, List<String>> serviceMap = new HashMap<>();

    /**
     * 基本路径
     *
     * @return
     */
    public String getBasePath() {
        return BasePath;
    }

    /**
     * 设置基本路径
     *
     * @param basePath
     */
    @Override
    public void setBasePath(String basePath) {
        BasePath = basePath;
    }


    @Override
    public void RegisterWatch(IDiscoveryWatch watch) {
        if (watch != null) {
            watchList.add(watch);
        }
    }

    @Override
    public void StartWatch() {
        LOG.warn("注册中心不支持监听");
    }

    @Override
    public void Close() {
        LOG.warn("注册中心无需关闭");
    }

    @Override
    public void registerService(String serviceName, String serverAddress, String metadata) throws RpcException {
        LOG.warn("todo:注册中心registerService");
    }

    @Override
    public void unRegisterService(String serviceName, String serverAddress) throws RpcException {
        LOG.warn("todo:注册中心unRegisterService");
    }

    @Override
    public void ServiceChange(Map<String, List<String>> serviceMap) {
        watchList.forEach((w) -> w.ServiceChange(serviceMap));
    }

    @Override
    public void RemoveService(String serviceName, String serverAddress) {
        watchList.forEach((w) -> w.RemoveService(serviceName, serverAddress));
    }

    /**
     * 解析注册中心路径为服务
     *
     * @param path
     * @param del
     */
    protected void parseNodePathToService(String path, boolean del) {
        String servicePath = path.substring(getBasePath().length());
        servicePath = StringUtils.trimString(servicePath, "/");
        int lastIndex = servicePath.lastIndexOf("/");
        if (lastIndex >= servicePath.length()) {
            return;
        }
        String serviceName = servicePath.substring(0, lastIndex);
        String serviceAddr = servicePath.substring(lastIndex + 1);
        List<String> serviceAddrList = serviceMap.get(serviceName);
        if (serviceAddrList == null) {
            serviceAddrList = new LinkedList<>();
            serviceMap.put(serviceName, serviceAddrList);
        }
        if (del) {
            LOG.debug("移除服务:" + serviceName + " => " + serviceAddr);
            serviceAddrList.remove(serviceAddr);
            RemoveService(serviceName, serviceAddr);
            return;
        } else if (serviceAddrList.contains(serviceAddr)) { //已经存在的忽略
            return;
        }
        LOG.debug("发现服务:" + serviceName + " => " + serviceAddr);
        serviceAddrList.add(serviceAddr);
        ServiceChange(serviceMap);
    }

}
