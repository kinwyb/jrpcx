package com.heldiam.jrpcx.core.discovery;

import com.heldiam.jrpcx.core.common.RpcException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

/**
 * 注册中心接口
 */
public interface IDiscovery {

    /**
     * 获取服务地址列表
     *
     * @return
     */
    List<String> getServices(String ServiceName);

    /**
     * 增加监控
     *
     * @param watch
     */
    void RegisterWatch(IDiscoveryWatch watch);

    /**
     * 开启变动监听
     */
    void StartWatch();

    /**
     * 关闭
     */
    void Close();

    /**
     * 基础路径
     * @param basePath
     */
    void setBasePath(String basePath);

    /**
     * 注册服务
     *
     * @param serviceName
     * @param metadata
     * @throws RpcException
     */
    void registerService(String serviceName, String serverAddress, String metadata) throws RpcException;

    /**
     * 取消注册服务
     *
     * @param serviceName
     * @throws RpcException
     */
    void unRegisterService(String serviceName, String serverAddress) throws RpcException;

    /**
     * 解析注册地址
     *
     * @param address
     * @return
     */
    static SocketAddress parseSocketAddress(String address) {
        String[] data = address.split(":");
        int port = 8972;
        if (data.length > 1) {
            port = Integer.valueOf(data[1]);
        }
        return new InetSocketAddress(data[0], port);
    }

}
