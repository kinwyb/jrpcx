package com.heldiam.jrpcx.client;

import com.heldiam.jrpcx.client.proxy.Proxy;
import com.heldiam.jrpcx.client.selector.Selector;
import com.heldiam.jrpcx.core.common.Feature;
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

    //连接的服务器地址标记
    private URL addressURL;

    /**
     * 负载均衡选择器
     */
    private final Selector selector;

    /**
     * 连接对象
     */
    private final Connect connect = new Connect();

    private Channel channel;

    private IDiscovery discovery;

    /**
     * 编码方式
     */
    private SerializeType serializeType = SerializeType.MsgPack;

    /**
     * 代理对象
     */
    private Proxy proxy;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Client.class.getName());

    public Client(Selector selector, @NotNull IDiscovery discovery) {
        this.selector = selector;
        this.discovery = discovery;
        discovery.StartWatch();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.Close()));
    }

    Channel getConnect(String serviceName, LinkedList<String> ignoreAddress) throws RpcException {
        URL address = this.addressURL;
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
        Channel channel = connect.doConnect(address);
        if (channel == null) {
            if (ignoreAddress == null) {
                ignoreAddress = new LinkedList<>();
            }
            ignoreAddress.add(address.toString());
            return getConnect(serviceName, ignoreAddress);
        }
        this.addressURL = address;
        this.channel = channel;
        return channel;
    }

    public void Call(String service,
                     String method,
                     Object param,
                     Feature feature) throws RpcException {
        try {
            getConnect(service, null);
        } catch (RpcException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RpcException(ex);
        }
//        LOG.debug("请求服务地址:" + channel.remoteAddress());
        Command data;
        try {
            data = connect.getHandler().coder.getRequest(service, method, param, serializeType, feature);
        } catch (Exception ex) {
            LOG.debug("消息序列化失败[" + ex.getMessage() + "]");
            throw new RpcException("消息序列化失败[" + ex.getMessage() + "]", ex, "Codec");
        }
        this.channel.writeAndFlush(data).addListener(f -> {
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
}
