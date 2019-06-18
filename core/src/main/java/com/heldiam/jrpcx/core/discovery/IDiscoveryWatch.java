package com.heldiam.jrpcx.core.discovery;

import java.util.List;
import java.util.Map;

/**
 * 服务变动监控
 */
public interface IDiscoveryWatch {

    /**
     * 服务地址变动
     *
     * @param serviceMap
     */
    void ServiceChange(Map<String, List<String>> serviceMap);

    /**
     * 服务移除
     *
     * @param serviceName
     * @param serverAddress
     */
    void RemoveService(String serviceName, String serverAddress);

}
