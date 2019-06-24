package com.heldiam.jrpcx.core.discovery;

import com.heldiam.jrpcx.core.common.Constants;
import com.heldiam.jrpcx.core.common.RpcException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author kinwyb
 * @date 2019-06-24 14:15
 **/
public class ZookeeperDiscovery extends BaseDiscovery implements Watcher {

    private ZooKeeper client;

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperDiscovery.class.getName());

    /**
     * 初始化zookeeper
     *
     * @param hosts
     * @throws IOException
     */
    public ZookeeperDiscovery(String... hosts) throws IOException {
        if (hosts == null || hosts.length < 1) {
            hosts = new String[]{"127.0.0.1:2181"};
        }
        // 创建一个与服务器的连接
        try {
            client = new ZooKeeper(String.join(",", hosts),
                    5000, null);
        } catch (Exception ex) {
            LOG.error("zookeeper连接失败:" + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<String> getServices(String ServiceName) {
        return serviceMap.get(ServiceName);
    }

    @Override
    public void StartWatch() {
        try {
            parseEtcdNode(getBasePath());
        } catch (Exception e) {
            LOG.error("zookeeper监听失败:" + e.getMessage());
        }
    }

    private void parseEtcdNode(String parent) throws Exception {
        List<String> childrens = client.getChildren(parent, this);
        if (childrens == null || childrens.isEmpty()) {
            parseNodePathToService(parent, false);
            return;
        }
        for (String child : childrens) {
            parseEtcdNode(parent + "/" + child);
        }
    }

    @Override
    public void RegisterWatch(IDiscoveryWatch watch) {
        super.RegisterWatch(watch);
        watch.ServiceChange(serviceMap);
    }

    @Override
    public void registerService(String serviceName, String serverAddress, String metadata) throws RpcException {
        try {
            createNode(getBasePath(), getBasePath());
            String servicePath = String.format("%s/%s", getBasePath(), serviceName);
            createNode(servicePath, servicePath);
            String node = String.format("%s/%s/%s", getBasePath(),
                    serviceName, serverAddress);
            createNode(node, metadata);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建节点
     *
     * @param nodePath
     * @param value
     * @throws KeeperException
     * @throws InterruptedException
     * @throws UnsupportedEncodingException
     */
    private void createNode(String nodePath, String value) throws KeeperException, InterruptedException, UnsupportedEncodingException {
        Stat state = client.exists(nodePath, false);
        if (state == null) {
            client.create(nodePath,
                    value.getBytes(Constants.CharsetName),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL); //临时节点,断开连接自动剔除
        }
    }

    @Override
    public void unRegisterService(String serviceName, String serverAddress) throws RpcException {
        try {
            String node = String.format("%s/%s/%s", getBasePath(),
                    serviceName, serverAddress);
            client.delete(node, -1);
        } catch (Exception e) {
            throw new RpcException("zookeeper节点删除失败");
        }
    }

    @Override
    public void Close() {
        super.Close();
        if (client != null) {
            try {
                client.close();
            } catch (InterruptedException e) {
                LOG.error("zookeeper关闭失败:" + e.getMessage());
            }
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                return;
            case NodeDeleted:
                try {
                    parseNodePathToService(watchedEvent.getPath(), true);
                } catch (Exception e) {
                    LOG.error("zookeeper监听事件处理错误:" + e.getMessage());
                }
                break;
            case NodeCreated:
            case NodeChildrenChanged:
                try {
                    parseEtcdNode(watchedEvent.getPath());
                    client.exists(watchedEvent.getPath(), this);
                } catch (Exception e) {
                    LOG.error("zookeeper监听事件处理错误:" + e.getMessage());
                }

        }
    }
}
