package com.heldiam.jrpcx.core.discovery;

import com.heldiam.jrpcx.core.common.RpcException;
import com.heldiam.jrpcx.core.common.StringUtils;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.requests.EtcdKeyPutRequest;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * @author kinwyb
 * @date 2019-06-17 11:27
 **/
public class EtcdV2Discovery extends BaseDiscovery {

    private EtcdClient client;
    private Map<String, List<String>> serviceMap = new HashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(EtcdV2Discovery.class.getName());

    public EtcdV2Discovery(String... etcdHosts) {
        ArrayList<URI> urls = new ArrayList<>();
        Arrays.stream(etcdHosts).forEach(v -> urls.add(URI.create("http://" + v)));
        client = new EtcdClient(urls.toArray(new URI[]{}));
    }

    private boolean shutdown = false;

    @Override
    public List<String> getServices(String ServiceName) {
        return serviceMap.get(ServiceName);
    }

    @Override
    public void StartWatch() {
        getServiceList();
        new Thread(() -> etcdWatch()).start();
    }

    private void etcdWatch() {
        if (shutdown) {
            return;
        }
        try {
            client.getDir(getBasePath()).recursive().waitForChange()
                    .consistent().send().addListener(promisea -> {
                LOG.debug("etcd数据变动");
                try {
                    EtcdKeysResponse response = promisea.get();
                    boolean del = false;
                    switch (response.action) {
                        case delete:
                        case expire:
                            del = true;
                    }
                    parseEtcdNode(response.node, del);
                } catch (Exception e) {
                    LOG.warn("etcd监听异常:" + e.getMessage());
                }
                new Thread(() -> etcdWatch()).start();
            });
        } catch (Exception e) {
            if (shutdown) { //关闭的直接返回
                return;
            }
            LOG.error("etcd监听异常:" + e.getMessage(), e);
            new Thread(() -> etcdWatch()).start();
        }
    }

    /**
     * 获取服务列表
     */
    private void getServiceList() {
        LOG.debug("获取etcd服务列表");
        try {
            EtcdKeysResponse response = client.get(getBasePath()).recursive().send().get();
            parseEtcdNode(response.node, false);
        } catch (Exception e) {
            LOG.error("Etcd服务列表获取失败:" + e.getMessage());
        }
    }

    private void parseEtcdNode(EtcdKeysResponse.EtcdNode node, boolean del) {
        if (node.isDir()) {
            node.nodes.forEach((n) -> parseEtcdNode(n, del));
            return;
        }
        String servicePath = node.key.substring(getBasePath().length());
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
            super.RemoveService(serviceName, serviceAddr);
            return;
        } else if (serviceAddrList.contains(serviceAddr)) { //已经存在的忽略
            return;
        }
        LOG.debug("发现服务:" + serviceName + " => " + serviceAddr);
        serviceAddrList.add(serviceAddr);
        super.ServiceChange(serviceMap);
    }

    @Override
    public void Close() {
        shutdown = true;
        try {
            client.close();
        } catch (IOException e) {
            LOG.warn("etcd关闭失败");
        }
    }

    @Override
    public void registerService(String serviceName, String serverAddress, String metadata) throws RpcException {
        SendIgnoreNotAFileException(client.putDir(getBasePath()));
        SendIgnoreNotAFileException(client.putDir(String.format("%s/%s", getBasePath(), serviceName)));
        try {
            String node = String.format("%s/%s/%s", getBasePath(),
                    serviceName, serverAddress);
            client.put(node, metadata).send().get();
        } catch (Exception e) {
            throw new RpcException("服务注册失败:" + e.getMessage());
        }
    }

    /**
     * 或略Not a file错误
     *
     * @param request
     * @throws RpcException
     */
    private void SendIgnoreNotAFileException(EtcdKeyPutRequest request) throws RpcException {
        try {
            request.send().get();
        } catch (EtcdException e) {
            if (e.errorCode != 102) { // not a file
                throw new RpcException("Etcd请求失败:" + e.getMessage());
            }
        } catch (Exception e) {
            throw new RpcException("Etcd请求失败:" + e.getMessage());
        }
    }

    @Override
    public void unRegisterService(String serviceName, String serverAddress) throws RpcException {
        try {
            client.delete(String.format("%s/%s/%s", getBasePath(), serviceName, serverAddress)).send().get();
        } catch (Exception e) {
            throw new RpcException("服务注册失败:" + e.getMessage());
        }
    }

}
