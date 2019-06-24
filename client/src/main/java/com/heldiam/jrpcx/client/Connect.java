package com.heldiam.jrpcx.client;

import com.heldiam.jrpcx.core.codec.NettyDecoder;
import com.heldiam.jrpcx.core.codec.NettyEncoder;
import com.heldiam.jrpcx.core.common.URL;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器连接类
 *
 * @author heldiam
 */
class Connect {

    static final EventLoopGroup GROUP = new NioEventLoopGroup();
    static final Bootstrap BOOT = new Bootstrap().group(GROUP).channel(NioSocketChannel.class);

    private final int maxReconnect = 5; //最大重连次数
    private static final Logger LOG = LoggerFactory.getLogger(Connect.class.getName());
    private final ConnectHandler handler = new ConnectHandler();
    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    Connect() {
        BOOT.option(ChannelOption.SO_KEEPALIVE, true);
        BOOT.option(ChannelOption.TCP_NODELAY, true);
        BOOT.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new NettyDecoder());
                ch.pipeline().addLast(new NettyEncoder());
                ch.pipeline().addLast(handler);
            }
        });
    }

    public ConnectHandler getHandler() {
        return handler;
    }

    /**
     * 获取一个连接
     *
     * @param remote
     * @return
     */
    public Channel doConnect(URL remote) {
        String remoteChannelKey = remote.toString();
        Channel channel = channelMap.get(remoteChannelKey);
        Integer reconnectNum = 1;
        while (true) {
            if (channel == null || !channel.isWritable()) {
                LOG.debug("连接无效,准备重连");
                if (reconnectNum > maxReconnect) {
                    LOG.warn("重连次数已达上限,更换服务器地址后再次连接");
                    channelMap.remove(remoteChannelKey);
                    return null;
                }
                channelClose(channel);
                try {
                    ChannelFuture f = BOOT.connect(new InetSocketAddress(remote.getHost(),
                            remote.getPort())).syncUninterruptibly();
                    channel = f.channel();
                    if (f.isSuccess()) {
                        channel = f.channel();
                        channelMap.put(remoteChannelKey, channel);
                        return channel;
                    } else {
                        LOG.debug("连接失败,准备重试:" + f.cause().getMessage());
                        reconnectNum++;
                        continue;
                    }
                } catch (Exception ex) {
                    LOG.debug("连接失败,准备重试:" + ex.getMessage());
                    reconnectNum++;
                    continue;
                }

            }
            channelMap.put(remoteChannelKey, channel);
            return channel;
        }
    }

    private void channelClose(Channel channel) {
        if (channel != null && channel.isActive()) {
            channel.flush();
            channel.disconnect();
            channel.deregister();
            channel.close();
        }
    }

    /**
     * 关闭
     */
    public void Close() {
        if (!GROUP.isShuttingDown() && !GROUP.isShutdown()) {
            GROUP.shutdownGracefully();
        }
        channelMap.forEach((k, v) -> channelClose(v));
        channelMap.clear();
        if (handler != null) {
            handler.Close();
        }
    }

}
