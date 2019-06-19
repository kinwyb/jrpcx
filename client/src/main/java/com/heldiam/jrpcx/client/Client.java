package com.heldiam.jrpcx.client;

import com.heldiam.jrpcx.client.failMode.FailModeEnum;
import com.heldiam.jrpcx.client.proxy.Proxy;
import com.heldiam.jrpcx.client.selector.Selector;
import com.heldiam.jrpcx.core.codec.Coder;
import com.heldiam.jrpcx.core.common.RpcException;
import com.heldiam.jrpcx.core.common.URL;
import com.heldiam.jrpcx.core.discovery.IDiscovery;
import com.heldiam.jrpcx.core.protocol.Command;
import com.heldiam.jrpcx.core.protocol.SerializeType;
import com.sun.istack.internal.NotNull;
import io.netty.channel.Channel;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * @author kinwyb
 * @date 2019-06-14 17:13
 **/
public class Client {

    /**
     * 负载均衡选择器
     */
    private final Selector selector;

    /**
     * 连接对象
     */
    private final Connect connect = new Connect();

    private IDiscovery discovery;

    /**
     * 编码方式
     */
    private SerializeType serializeType = SerializeType.MsgPack;

    /**
     * 代理对象
     */
    private Proxy proxy;

    /**
     * 失败重试模式
     */
    private FailModeEnum failMode = FailModeEnum.Failtry;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Client.class.getName());

    public Client(Selector selector, @NotNull IDiscovery discovery) {
        this.selector = selector;
        this.discovery = discovery;
        discovery.StartWatch();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.Close()));
    }

    Channel getConnect(String serviceName, @NotNull LinkedList<String> ignoreAddress) throws RpcException {
        URL address = null;
        for (int i = 0; i < 10; i++) { //选取地址,10次选不出来就提示无有效服务地址
            address = selector.GetAddress(serviceName);
            if (address == null) {
                continue;
            }
            // 跳过忽略的IP地址
            if (ignoreAddress != null && ignoreAddress.contains(address.toString())) {
                address = null;
                continue;
            }
            break;
        }
        if (address == null) {
            LOG.error("无有效的服务地址");
            throw new RpcException("无有效的服务地址");
        }
        ignoreAddress.add(address.toString()); //当前地址加入忽略列表,已便失败重试时可以选择新地址
        Channel channel = connect.doConnect(address);
        if (channel == null) {
            return getConnect(serviceName, ignoreAddress);
        }
        return channel;
    }

    /**
     * 远程调用
     *
     * @param feature 请求对象
     * @return 当前调用服务地址
     * @throws RpcException
     */
    public void Call(Feature feature) throws RpcException {
        Channel channel = getConnect(feature.seviceName, feature.ignoreAddress);
//        LOG.debug("请求服务地址:" + channel.remoteAddress());
        Command data = Coder.getRequest(feature.seviceName, feature.methodName,
                feature.params, serializeType, feature.seq);
        channel.writeAndFlush(data).addListener(f -> {
            if (!f.isSuccess()) {
                LOG.debug("服务请求发送失败");
            }
        });
    }

    /**
     * 创建一个代理对象
     *
     * @param <T>
     * @param t
     * @return
     */
    public <T> T Proxy(Class<T> t) {
        if (proxy == null) {
            proxy = new Proxy(this);
        }
        return proxy.createProxy(t);
    }


    /**
     * 关闭
     */
    public void Close() {
        LOG.debug("Rpcx Client 关闭");
        connect.Close();
        if (discovery != null) {
            discovery.Close();
        }
    }

    public void setDiscovery(IDiscovery discovery) {
        this.discovery = discovery;
    }

    /**
     * 设在失败模式
     *
     * @param failMode
     * @return
     */
    public Client setFailMode(FailModeEnum failMode) {
        this.failMode = failMode;
        return this;
    }

    /**
     * 获取失败模式
     *
     * @return
     */
    public FailModeEnum getFailMode() {
        return failMode;
    }
}
